package com.yude.game.doudizhu.domain;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yude.game.common.constant.PlayerStatusEnum;
import com.yude.game.common.manager.IPushManager;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.AbstractRoomModel;
import com.yude.game.common.model.Player;
import com.yude.game.doudizhu.application.response.*;
import com.yude.game.doudizhu.application.response.dto.*;
import com.yude.game.doudizhu.constant.CommonConstant;
import com.yude.game.doudizhu.constant.RuleConfig;
import com.yude.game.doudizhu.constant.command.PushCommandCode;
import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.constant.status.SeatStatusEnum;
import com.yude.game.doudizhu.domain.action.BaseRoomProcess;
import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.doudizhu.domain.card.CardType;
import com.yude.game.doudizhu.domain.card.CardTypeInfo;
import com.yude.game.doudizhu.domain.history.*;
import com.yude.game.doudizhu.timeout.DdzTimeoutTask;
import com.yude.game.doudizhu.timeout.DdzTimeoutTaskPool;
import com.yude.game.doudizhu.util.DdzTable;
import com.yude.game.exception.BizException;
import com.yude.game.exception.SystemException;
import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: HH
 * @Date: 2020/6/29 15:40
 * @Version: 1.0
 * @Declare:
 */
public class DouDiZhuRoom extends AbstractRoomModel<DouDiZhuZone, DouDiZhuSeat, DdzTimeoutTaskPool> implements BaseRoomProcess,Cloneable {
    public static final Logger log = LoggerFactory.getLogger(DouDiZhuRoom.class);


    private DdzTimeoutTask ddzTimeoutTask;
    {
        timeoutTaskPool = DdzTimeoutTaskPool.getInstance();
    }

    @Override
    public GameStatusEnum getGameStatus() {
        return gameZone.getGameStatus();
    }

    @Override
    public DouDiZhuSeat getPracticalSeatModle(Player player, int posId) {
        return new DouDiZhuSeat(player, posId);
    }

    @Override
    public DouDiZhuZone getPracticalGameZoneModel() {
        //进行下一局的时候
        int gameRound = 1;
        int gameInning = 1;
        if (gameZone != null) {
            if (gameZone.getInning() + 1 > inningLimit) {
                gameRound = gameZone.getRound() + 1;
            } else {
                gameInning = gameZone.getInning() + 1;
            }
        }

        /**
         * 给游戏域 和 位置 建立关联关系：聚合关系。与room中指向的是同一个位置对象
         */
        DouDiZhuSeat[] seatModels = new DouDiZhuSeat[posIdSeatMap.size()];
        int i = 0;
        for (Object seat : posIdSeatMap.values()) {
            DouDiZhuSeat seatModel = (DouDiZhuSeat) seat;
            seatModels[i] = seatModel;
            i++;
        }

        return new DouDiZhuZone(seatModels, gameRound, gameInning);
    }

    @Override
    public void startGame() {
        this.gameZone = getPracticalGameZoneModel();
        log.info("游戏开始：roomId={} zoneId={}", roomId, gameZone.getZoneId());
        gameZone.init(); //游戏状态设置为开始
        gameZone.start(roomId); //游戏状态设置为叫分
        noticePlayersDealCardResult();
        noticePlayersCallScoreOption();

        int time = (RuleConfig.ANIMATION_CALL_SCORE_DELAYED + getGameStatus().getTimeoutTime()) * 1000;
        ddzTimeoutTask = new DdzTimeoutTask(time, this);
        timeoutTaskPool.addTask(ddzTimeoutTask);

    }

    public boolean reStartGame() {
        //重开过一次不能再重开
        if (gameZone.isReStartGame()) {
            gameZone.becomeLandlordForcedFirstPlayer();
            return false;
        }
        log.info("重开局： roomId={} zoneId={}", roomId, gameZone.getZoneId());
        for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
            DouDiZhuSeat seat = entry.getValue();
            seat.reset();
        }
        startGame();
        gameZone.setReStartGame();
        return true;
    }

    public void callScore(int posId, int score) {
        log.info("玩家叫分：roomId={} zoneId={} posId={}  score={}", roomId, gameZone.getZoneId(), posId, score);

        DouDiZhuSeat ddzSeat = posIdSeatMap.get(posId);
        if (!SeatStatusEnum.CALL_SCORE.equals(ddzSeat.getStatus())) {
            log.warn("当前玩家位置状态不能叫分：roomId={} posId={}  score={}", roomId, posId, score);
            throw new BizException("玩家位置当前不是叫分状态");
        }
        if (gameZone.getCurrentOperatorPosId() != posId) {
            log.warn("当前玩家没有叫分权限：roomId={} posId={}  score={}", roomId, posId, score);
            throw new BizException("还没有轮到该玩家进行操作");
        }

        if (!RuleConfig.isExistsCallScoreOpion(score)) {
            throw new BizException(StatusCodeEnum.ILLEGAL_CALL_SCORE);
        }

        //0分是不叫...
        if (score <= gameZone.getBeforeCallScore() && score != 0) {
            log.info("roomId={}  玩家叫分：{}  之前的分：{}", roomId, score, gameZone.getBeforeCallScore());
            throw new BizException(StatusCodeEnum.CALL_SCORE_ERRO);
        }
        //把当前步骤的超时任务标记为失效
        timeoutTaskPool.addUseLessTask(roomId, getStep());

        ddzSeat.setCallScore(score);
        ddzSeat.setStatus(SeatStatusEnum.REDOUBLE);
        CallScoreStep callScoreStep = new CallScoreStep(gameZone.getStepCount(), posId, score, new ArrayList<>(ddzSeat.getHandCardList()), gameZone.getGameStatus());

        Integer landlordPosId = gameZone.callScore(posId, score);
        buildStep(callScoreStep, posId);

        noticePlayersCallScoreResult(ddzSeat.getUserId(), callScoreStep);

        if (landlordPosId == null) {
            noticePlayersCallScoreOption();
            ddzTimeoutTask = new DdzTimeoutTask(this);
            timeoutTaskPool.addTask(ddzTimeoutTask);
            return;
        }
        DouDiZhuSeat landlordSeat = posIdSeatMap.get(landlordPosId);
        if (landlordSeat.getCallScore().get() == 0) {
            //三个玩家都不叫，重新发牌
            timeoutTaskPool.uselessTaskMapClear(roomId);
            final boolean isNeed = reStartGame();
            if (isNeed) {
                return;
            }
        }
        for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
            DouDiZhuSeat seat = entry.getValue();
            if (SeatStatusEnum.CALL_SCORE.equals(seat.getStatus())) {
                log.debug("roomId={}  zoneId={} posId={} userrId={} 未叫分就已经被别人三分拿下地主了", roomId, gameZone.getZoneId(), seat.getPosId(), seat.getUserId());
                seat.setStatus(SeatStatusEnum.REDOUBLE);
            }
        }
        DouDiZhuSeat seat = posIdSeatMap.get(landlordPosId);
        seat.getHandCardList().addAll(Arrays.asList(gameZone.getHoleCards()));
        CardType.sort(seat.getHandCardList());
        noticePlayersLandlordOwnershipResult(landlordPosId, gameZone.getLandlordScore(), gameZone.getHoleCards());

        noticePlayerCurrentOperator(getGameStatus().getTimeoutTime());
        noticePlayersRedoubleOption(false);

        int time = (RuleConfig.ANIMATION_LANDLORD_OWNERSHIP_DELAYED + getGameStatus().getTimeoutTime()) * 1000;
        ddzTimeoutTask = new DdzTimeoutTask(time, this);
        timeoutTaskPool.addTask(ddzTimeoutTask);
    }

    public void redouble(int posId, int redoubleNum) {
        // 农民看不见互相叫加倍：同时公布两农民加倍结果
        log.info("玩家执行加倍操作流程：roomId={} zoneId={} posId={}  redouble={}", roomId, gameZone.getZoneId(), posId, redoubleNum);

        DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(posId);
        if (!SeatStatusEnum.REDOUBLE.equals(douDiZhuSeat.getStatus())) {
            throw new BizException("当前位置状态不能进行 加倍流程操作");
        }

        if (!gameZone.getCurrentNoticePosIds().contains(posId)) {
            log.warn("当前玩家没有加倍操作权限：roomId={} posId={}  redoubleNum={}", roomId, posId, redoubleNum);
            throw new BizException("还没有轮到该玩家进行操作");
        }

        if (!Arrays.stream(gameZone.getRedoubleOption()).filter(option -> option == redoubleNum).findFirst().isPresent()) {
            log.warn("roomId={} 加倍操作 不存在该选项", roomId);
            throw new BizException("加倍操作 不存在该选项");
        }
        //在校验完再清除吧 (超时机制 版本1)
        timeoutTaskPool.addUseLessTask(roomId, getStep());

        int landlordPosId = gameZone.getLandlordPosId();
        if (posId == landlordPosId) {
            landlordRedouble(posId, landlordPosId, douDiZhuSeat, redoubleNum);
            ddzTimeoutTask = new DdzTimeoutTask(this);
            timeoutTaskPool.addTask(ddzTimeoutTask);
            return;
        }

        long lastOperationTime = gameZone.getLastOperationTime();
        boolean isFinish = farmersRedouble(posId, landlordPosId, douDiZhuSeat, redoubleNum);
        long nowTime = System.currentTimeMillis();
        long remainingTime = TimeUnit.MILLISECONDS.convert(getGameStatus().getTimeoutTime() + RuleConfig.ANIMATION_LANDLORD_OWNERSHIP_DELAYED, TimeUnit.SECONDS) + lastOperationTime  - nowTime;

        if (!isFinish) {
            //存在着时间差，有个临界点
            log.info("roomId={} 加倍操作剩余时间： {}", roomId, remainingTime);
            remainingTime = remainingTime > 0 ? remainingTime : 100;
            ddzTimeoutTask = new DdzTimeoutTask(remainingTime, this);
            timeoutTaskPool.addTask(ddzTimeoutTask);
        }
    }

    private boolean farmersRedouble(int posId, int landlordPosId, DouDiZhuSeat douDiZhuSeat, int redoubleNum) {
        log.info("农民加倍操作：roomId={} zoneId={} posId={}  landlordPosId={}  redoubleNum={}", roomId, gameZone.getZoneId(), posId, landlordPosId, redoubleNum);

        douDiZhuSeat.setRedoubleNum(redoubleNum);
        douDiZhuSeat.setStatus(SeatStatusEnum.OPERATION_CARD);
        RedoubleStep redoubleStep = new RedoubleStep(gameZone.getStepCount(), posId, redoubleNum, new ArrayList(douDiZhuSeat.getHandCardList()), gameZone.getGameStatus());

        boolean isFinish = gameZone.farmersRedouble(redoubleNum);
        buildStep(redoubleStep, posId);

        if (isFinish) {
            List<RedoubleDTO> redoubleDTOList = new ArrayList<>();
            for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
                Integer seatPosId = entry.getKey();
                DouDiZhuSeat seat = entry.getValue();
                if (seatPosId != landlordPosId) {
                    RedoubleDTO redoubleDTO = new RedoubleDTO(redoubleStep.getStep(), seatPosId, seat.getRedoubleNum().get());
                    redoubleDTOList.add(redoubleDTO);
                }

            }

            FarmersRedoubleResponse response = new FarmersRedoubleResponse(redoubleDTOList, gameZone.getGameStatus().status());
            noticePlayersRedoubleResult(PushCommandCode.FARMERS_REDOUBLE, response);

            if (gameZone.isNeedLandlordRedoubleOperation()) {
                log.info("地主需要进行加倍操作: roomId={} zoneId={}", roomId, gameZone.getZoneId());
                //通知地主加倍
                noticePlayersRedoubleOption(true);
                ddzTimeoutTask = new DdzTimeoutTask(this);
                timeoutTaskPool.addTask(ddzTimeoutTask);
                return false;
            }
            DouDiZhuSeat landlordSeat = posIdSeatMap.get(landlordPosId);
            landlordSeat.setStatus(SeatStatusEnum.OPERATION_CARD);
            noticePlayersRedoubleDetail();
            noticePlayerCurrentOperator(getGameStatus().getTimeoutTime());
            ddzTimeoutTask = new DdzTimeoutTask(this);
            timeoutTaskPool.addTask(ddzTimeoutTask);
        }

        return isFinish;
    }

    private void landlordRedouble(int posId, int landlordPosId, DouDiZhuSeat douDiZhuSeat, int redoubleNum) {
        log.info("地主加倍操作：roomId={} zoneId={} posId={}  landlordPosId={}  redoubleNum={}", roomId, gameZone.getZoneId(), posId, landlordPosId, redoubleNum);
        for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
            DouDiZhuSeat seat = entry.getValue();
            if (posId == landlordPosId) {
                continue;
            }
            //也可以用位置状态标识
            if (seat.getRedoubleNum() == null) {
                throw new BizException("地主需要等待农民选择翻倍");
            }
        }
        douDiZhuSeat.setRedoubleNum(redoubleNum);
        douDiZhuSeat.setStatus(SeatStatusEnum.OPERATION_CARD);

        RedoubleStep redoubleStep = new RedoubleStep(gameZone.getStepCount(), posId, redoubleNum, new ArrayList(douDiZhuSeat.getHandCardList()), gameZone.getGameStatus());
        boolean isFinishRedouble = gameZone.landownersRedouble();
        buildStep(redoubleStep, posId);

        if (!isFinishRedouble) {
            log.error("意料之外的错误：地主执行完加倍操作后。还有玩家未执行加倍操作 roomId={} gameZone = {}", roomId, gameZone);
            throw new SystemException("意料之外的错误：地主执行完加倍操作后。还有玩家未执行加倍操作");
        }

        /**
         * 前面buildStep后已经把stepCount加1了。所以要使用redoubleStep里的step值
         */
        RedoubleDTO redoubleDTO = new RedoubleDTO(redoubleStep.getStep(), posId, redoubleNum);
        LandownersRedoubleResponse response = new LandownersRedoubleResponse(redoubleDTO, gameZone.getGameStatus().status());
        noticePlayersRedoubleResult(PushCommandCode.LANDOWNERS_REDOUBLE, response);
        noticePlayersRedoubleDetail();
        noticePlayerCurrentOperator(getGameStatus().getTimeoutTime());
    }

    public void operationCard(int posId, List<Integer> cardList) {
        log.info("玩家操作牌：roomId={} zoneId={} posId={}  cardList={}", roomId, gameZone.getZoneId(), posId, cardList);
        DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(posId);
        Integer currentOperatorPosId = gameZone.getCurrentOperatorPosId();
        if (posId != gameZone.getCurrentOperatorPosId()) {
            log.error("该玩家没有操作权：roomId={} ", roomId);
            throw new BizException("还没有轮到你操作 posId=" + posId + " currentPosId=" + currentOperatorPosId);
        }
        int size = cardList.size();

        List<Integer> handCardList = douDiZhuSeat.getHandCardList();
        if (size != 0 && !isExistsAll(handCardList, cardList)) {
            log.error("roomId={} 出牌参数中，有的牌在手牌中不存在 param={}  handcard={}", roomId,cardList,handCardList);
            throw new BizException(StatusCodeEnum.ILLEGAL_CARD);
        }
        timeoutTaskPool.addUseLessTask(roomId, getStep());
        douDiZhuSeat.setOutCardTips(null);
        //不出
        if (size == 0) {
            pass(douDiZhuSeat);
            log.info("玩家剩余牌：roomId={} zoneId={} handCards={}", roomId, gameZone.getZoneId(), douDiZhuSeat.getHandCardList());
            return;
        }


        outCard(douDiZhuSeat, cardList);
        log.info("玩家剩余牌：roomId={} zoneId={} handCards={}", roomId, gameZone.getZoneId(), douDiZhuSeat.getHandCardList());
    }

    /**
     * 出牌
     *
     * @param douDiZhuSeat
     */
    private void pass(DouDiZhuSeat douDiZhuSeat) {
        int posId = douDiZhuSeat.getPosId();
        gameZone.operationCard(posId, null);
        if (!gameZone.canPass(posId)) {
            log.warn("该玩家不能不出牌：roomId={} posId={}", roomId, douDiZhuSeat.getPosId());
            throw new BizException(StatusCodeEnum.MUST_OUT_CARD);
        }
        OperationCardStep operationCardStep = new OperationCardStep(gameZone.getStepCount(), posId, douDiZhuSeat.getHandCardList().size(), new ArrayList(douDiZhuSeat.getHandCardList()), gameZone.getGameStatus());

        OperationCardResponse response = new OperationCardResponse(operationCardStep.getStep(), posId, operationCardStep.getRemainingCardSize());
        buildStep(operationCardStep, posId);

        noticePlayersOperationCardResult(response);

        Card lastOutCard = gameZone.getLastOutCard();
        nextPlayerOutCardTips(lastOutCard.getCardType().getCardKey());
    }

    private void outCard(DouDiZhuSeat seat, List<Integer> cards) {
        Card lastOutCard = gameZone.getLastOutCard();
        CardTypeInfo cardType = DdzTable.judgeCardType(cards);
        log.debug("判断牌型： roomId={} cardType={}", roomId, cardType);
        if (cardType == null) {
            log.error("没有匹配的牌型：roomId={} userId={} posId={} cards={}", roomId, seat.getUserId(), seat.getPosId(), cards);
            throw new SystemException("牌型不存在：" + cards);
        }

        if (lastOutCard == null) {
            Card card = new Card(cards, cardType);
            //第一手牌，直接出
            buildAndNoticeOperationCard(seat, card);
            return;
        }
        if (seat.getPosId() == gameZone.getLastOutCardPosId()) {
            Card card = new Card(cards, cardType);
            //上一轮自己出牌最大，直接出
            buildAndNoticeOperationCard(seat, card);
            return;
        }

        Card card = new Card(cards, cardType);
        boolean isCanOutCard = DdzTable.compareCard(card, lastOutCard);
        if (isCanOutCard) {
            buildAndNoticeOperationCard(seat, card);
            return;
        }
        log.info("不能出这组牌：roomId={} seat={} card={}  lastOutCard={} handCards={}", roomId, seat, card, lastOutCard, seat.getHandCardList());
        throw new BizException("不能出这组牌");
    }

    private void buildAndNoticeOperationCard(DouDiZhuSeat seat, Card card) {
        int outCardPosId = seat.getPosId();
        List<Integer> outCardList = card.getCards();
        List<Integer> handCardList = seat.getHandCardList();
        for (Integer cardIndex : card.getCards()) {
            handCardList.remove(cardIndex);
        }

        OperationCardStep operationCardStep = new OperationCardStep(gameZone.getStepCount(), outCardPosId, card, seat.getHandCardList().size(), new ArrayList(seat.getHandCardList()), gameZone.getGameStatus());

        final boolean isBomb = gameZone.operationCard(seat.getPosId(), card);
        buildStep(operationCardStep, outCardPosId);

        OperationCardResponse response = new OperationCardResponse();
        CardDTO cardDTO = new CardDTO(outCardList, card.getCardType().getType());
        response.setStep(operationCardStep.getStep())
                .setPosId(outCardPosId)
                .setRemainingCardSize(operationCardStep.getRemainingCardSize())
                .setCards(cardDTO);

        noticePlayersOperationCardResult(response);
        if (isBomb) {
            noticePlayersRedoubleDetail();
        }

        int remainingSize = handCardList.size();
        if (remainingSize <= RuleConfig.ALARM) {
            if (handCardList.size() == 0) {
                gameOver(outCardPosId);
                return;
            }
            /**
             * 报双，报单，只报警一次
             */
            if(seat.isCanAlarm()){
                noticePlayersAlarm(outCardPosId);
                seat.setCanAlarm(false);
            }

        }
        nextPlayerOutCardTips(card.getCardType().getCardKey());
    }

    private void nextPlayerOutCardTips(String key) {
        log.debug("roomId={} card key = {}", roomId, key);
        DouDiZhuSeat seat = posIdSeatMap.get(gameZone.getNextPosIdInfo());
        List<CardDTO> effectiveList = new ArrayList<>();

        boolean isSelf = gameZone.getLastOutCardPosId() == seat.getPosId();
        if (!isSelf) {
            /**
             * 跟牌才有提示
             */
            List<CardDTO> cardsGreaterThan = DdzTable.getCardsGreaterThan(key);
            if (cardsGreaterThan.size() > 0) {
                List<Integer> handCardList = seat.getHandCardList();
                log.debug("roomId={} handCardIndexs = {}", roomId, handCardList);
                List<Integer> handCardValues = DdzTable.cardIndexConvertcardValue(handCardList);
                log.debug("roomId={} handCardValues = {}", roomId, handCardValues);
                for (CardDTO cardDTO : cardsGreaterThan) {
                    log.debug("roomId={} Card = {}", roomId, cardDTO.getCards());
                    if (isExistsAll(handCardValues, cardDTO.getCards())) {
                        effectiveList.add(cardDTO);
                        log.debug("roomId={} Exists Cards = {}", roomId, effectiveList);
                    }
                }
                if (effectiveList.size() > 0) {
                    seat.setOutCardTips(effectiveList);
                    noticePlayerCurrentOperator(gameZone.getGameStatus().getTimeoutTime());
                    IPushManager pushManager = roomManager.getPushManager();
                    OutCardTipsResponse response = new OutCardTipsResponse(effectiveList);
                    pushManager.pushToUser(PushCommandCode.OUT_CARD_TIPS, seat.getUserId(), response, roomId);
                    ddzTimeoutTask = new DdzTimeoutTask(this);
                    timeoutTaskPool.addTask(ddzTimeoutTask);
                    return;
                }/*else {
                //如同上家出了王炸一样，要不起
            }*/

            }
        }


        IPushManager pushManager = roomManager.getPushManager();
        OutCardTipsResponse response = new OutCardTipsResponse(effectiveList);
        pushManager.pushToUser(PushCommandCode.OUT_CARD_TIPS, seat.getUserId(), response, roomId);
        if (isSelf) {
            noticePlayerCurrentOperator(gameZone.getGameStatus().getTimeoutTime());
            ddzTimeoutTask = new DdzTimeoutTask(this);
            timeoutTaskPool.addTask(ddzTimeoutTask);
        } else {
            noticePlayerCurrentOperator(RuleConfig.OPERATION_TIME_CAN_NOT_WIN);
            ddzTimeoutTask = new DdzTimeoutTask(RuleConfig.OPERATION_TIME_CAN_NOT_WIN * 1000, this);
            timeoutTaskPool.addTask(ddzTimeoutTask);
        }

    }

    private void gameOver(int winnerPosId) {
        log.info("游戏结束：roomId={}  zoneId={}", roomId, gameZone.getZoneId());
        gameZone.gameOver(winnerPosId);
        settlement();
        gameZone.clean();
        timeoutTaskPool.uselessTaskMapClear(roomId);
        clean();

    }

    private void settlement() {
        log.info("游戏结算：roomId={} zoneId={}", roomId, gameZone.getZoneId());

        isSpringTime();

        boolean landlordWin = gameZone.winnerCampisLandlord();
        int landlordPosId = gameZone.getLandlordPosId();
        DouDiZhuSeat landlordSeat = posIdSeatMap.get(landlordPosId);
        int sumScore = 0;
        int sumRedoubleNum = 0;
        int landlordScore = gameZone.getLandlordScore();
        for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
            int posId = entry.getKey();
            DouDiZhuSeat seat = entry.getValue();
            if (posId == gameZone.getLandlordPosId()) {
                continue;
            }
            Integer selfRedouble = seat.getRedoubleNum().get();
            int resultRedoubleValue;
            if (selfRedouble == 1) {
                resultRedoubleValue = selfRedouble * gameZone.getCommonRedoubleNum();
            } else {
                resultRedoubleValue = selfRedouble * gameZone.getCommonRedoubleNum() * landlordSeat.getRedoubleNum().orElse(1);
            }
            resultRedoubleValue = resultRedoubleValue == 0 ? 1 : resultRedoubleValue;
            int score = landlordScore * resultRedoubleValue;
            final int resultScore = landlordWin ? -score : score;
            sumScore -= resultScore;
            sumRedoubleNum += resultRedoubleValue;

            updatePlayerScore(seat, resultScore);
            SettlementStep settlementStep = new SettlementStep();
            settlementStep.setStep(gameZone.getStepCount())
                    .setPosId(posId)
                    //H2 叫的分 x 底注，现在房间配置还没有引入底注（就是倍数详情的第一个显示的属性）的概念,所以默认是1，叫分 x 1 = 叫分
                    .setBaseScore(gameZone.getLandlordScore())
                    .setResultRedoubleNum(resultRedoubleValue)
                    .setScoreChange(resultScore)
                    .setHandCards(new ArrayList<>(seat.getHandCardList()))
                    .setType(gameZone.getGameStatus());
            GameStepModel stepModel = new GameStepModel(gameZone.getZoneId(), seat.getPlayer(), settlementStep);
            gameZone.addHistory(stepModel);
        }
        updatePlayerScore(landlordSeat, sumScore);
        SettlementStep settlementStep = new SettlementStep();
        settlementStep.setStep(gameZone.getStepCount())
                .setPosId(landlordPosId)
                .setBaseScore(gameZone.getLandlordScore())
                .setResultRedoubleNum(sumRedoubleNum)
                .setScoreChange(sumScore)
                .setHandCards(new ArrayList<>(landlordSeat.getHandCardList()))
                .setType(gameZone.getGameStatus());
        GameStepModel stepModel = new GameStepModel(gameZone.getZoneId(), landlordSeat.getPlayer(), settlementStep);
        gameZone.addHistory(stepModel);

        noticePlayersSettlementResult();
        //FastJson转换的数据，需要有get set方法...
        log.info("游戏数据：roomId={} zone={}", roomId, JSONObject.toJSONString(gameZone, SerializerFeature.DisableCircularReferenceDetect));
        //log.info("游戏过程数据： roomId={} zoneId={}  history={}", roomId,gameZone.getZoneId(),JSONObject.toJSONString(gameZone.getOperationHistoryList(), SerializerFeature.DisableCircularReferenceDetect));
    }

    private void updatePlayerScore(DouDiZhuSeat seat, int changScore) {
        seat.getPlayer().scoreSettle(changScore);
    }


    public void reconnect(Long userId) {
        //1.给当前玩家下发游戏域的信息 -》 游戏状态  当前操作人
        //2.通知所有玩家该玩家重连
        log.info("玩家userId={} 重连： gameZone={}", userId, gameZone);

        Integer operatorPosId = userPosIdMap.get(userId);
        DouDiZhuSeat reconnectSeat = posIdSeatMap.get(operatorPosId);

        List<SeatInfoDTO> seatInfoDTOList = new ArrayList<>();
        for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
            Integer posId = entry.getKey();
            DouDiZhuSeat douDiZhuSeat = entry.getValue();
            Player player = douDiZhuSeat.getPlayer();

            List<Integer> currentHandCardList = new ArrayList<>();
            if (operatorPosId.equals(posId)) {
                currentHandCardList.addAll(douDiZhuSeat.getHandCardList());
            } else {
                for (int i = 0; i < douDiZhuSeat.getHandCardList().size(); ++i) {
                    //只是分配内存空间，传的时候size还是0，所以用-1填充
                    currentHandCardList.add(-1);
                }
            }
            PlayerDTO playerDTO = new PlayerDTO(player.getUserId(), player.getNickName(), player.getHeadUrl(), player.getScore());


            SeatInfoDTO seatInfoDTO = new SeatInfoDTO();
            seatInfoDTO.setPosId(posId)
                    .setPlayerDTO(playerDTO)
                    .setHandCards(currentHandCardList)
                    .setCallScore(douDiZhuSeat.getCallScore().orElse(-1))
                    .setRedouble(douDiZhuSeat.getRedoubleNum().orElse(-1))
                    .setOutCardTips(douDiZhuSeat.getTips());
            seatInfoDTOList.add(seatInfoDTO);
        }

        GameZoneInfoDTO gameZoneInfoDTO = new GameZoneInfoDTO();
        Card lastOutCard = gameZone.getLastOutCard();
        List<Integer> lastOutCards = lastOutCard == null ? null : lastOutCard.getCards();
        List<Integer> operatorList;
        if (GameStatusEnum.FARMERS_REDOUBLE.equals(gameZone.getGameStatus())) {
            operatorList = new ArrayList<>();
            for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
                int posId = entry.getKey();
                if (posId != gameZone.getLandlordPosId()) {
                    operatorList.add(posId);
                }
            }
        } else {
            operatorList = new ArrayList<>();
            operatorList.add(gameZone.getCurrentOperatorPosId());
        }
        CardDTO cardDTO = new CardDTO(lastOutCards, lastOutCard == null ? null : lastOutCard.getCardType().getType());

        long remainingTime = ddzTimeoutTask.getRemaining();

        DouDiZhuSeat landlordSeat = posIdSeatMap.get(gameZone.getLandlordPosId());
        gameZoneInfoDTO.setRoomId(roomId)
                .setZoneId(gameZone.getZoneId())
                .setGameStatus(getGameStatus().status())
                .setCurrentOperatorPosId(operatorList)
                .setLastOutCard(cardDTO)
                .setLastOutCardPosId(gameZone.getLastOutCardPosId())
                .setLastOperationPosId(gameZone.getBeforeOperatorPosId())
                .setLandlordPosId(gameZone.getLandlordPosId())
                .setLandlordScore(gameZone.getLandlordScore())
                .setHoleCards(gameZone.getLandlordPosId() == -1 ? null : Arrays.asList(gameZone.getHoleCards()))
                .setCallScoreOptions(gameZone.getCallScoreOption())
                .setRedoubleNumOptions(Arrays.asList(gameZone.getRedoubleOption()))
                .setRemaningTime(remainingTime)
                .setRedoubleDetail(gameZone.getRedoubleDetail(reconnectSeat, landlordSeat));


        SelfReconnectResponse reconnectResponse = new SelfReconnectResponse(gameZoneInfoDTO, seatInfoDTOList);


        IPushManager pushManager = roomManager.getPushManager();
        pushManager.pushToUser(PushCommandCode.SELF_RECONNECT, userId, reconnectResponse, roomId);

        OtherReconnectResponse otherReconnectResponse = new OtherReconnectResponse(operatorPosId);
        pushToRoomUser(PushCommandCode.OTHER_RECONNECT, otherReconnectResponse, userId);


    }

    /**
     * 发牌通知:这里取巧了，因为刚开局，没有其他类型的Step。 但是封装Step的地方最好一致，要么是一直在room中，要么是一直在gameZone中。明确职责
     */
    private void noticePlayersDealCardResult() {
        List<GameStepModel> operationHistory = gameZone.getOperationHistoryList();
        for (GameStepModel stepModel : operationHistory) {
            GameStartStep gameStartStep = (GameStartStep) stepModel.getOperationStep();
            GameStartResponse gameStartResponse = new GameStartResponse();
            gameStartResponse.setRoomId(roomId)
                    .setZoneId(gameZone.getZoneId())
                    .setStep(gameZone.getStepCount())
                    .setPosId(gameStartStep.getPosId())
                    .setCards(gameStartStep.getHandCards())
                    .setGameStatus(gameZone.getGameStatus().status());
            IPushManager pushManager = roomManager.getPushManager();
            pushManager.pushToUser(PushCommandCode.GAME_START, stepModel.getPlayers().getUserId(), gameStartResponse, roomId);
        }
    }

    /**
     * 叫分的可选列表: 前面的玩家叫分 会影响后面玩家 叫分的可选列表
     * 叫分的玩家
     */
    private void noticePlayersCallScoreOption() {
        noticePlayerCurrentOperator(getGameStatus().getTimeoutTime());


        List<Integer> callScoreOptionList = gameZone.getCallScoreOption();

        CallScoreOptionResponse response = new CallScoreOptionResponse(callScoreOptionList);
        int currentOperatorPosId = gameZone.getCurrentOperatorPosId();
        DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(currentOperatorPosId);

        IPushManager pushManager = roomManager.getPushManager();
        pushManager.pushToUser(PushCommandCode.CALL_SCORE_OPTION, douDiZhuSeat.getUserId(), response, roomId);
    }

    /**
     * 通知玩家 叫分的结果
     *
     * @param userId
     * @param callScoreStep
     */
    private void noticePlayersCallScoreResult(Long userId, CallScoreStep callScoreStep) {
        CallScoreResponse callScoreResponse = new CallScoreResponse(callScoreStep.getStep(), callScoreStep.getPosId(), callScoreStep.getScore());
        pushToRoomUser(PushCommandCode.CALL_SCORE, callScoreResponse);


    }

    /**
     * 通知玩家地主归属
     *
     * @param landlordPosId
     * @param holeCards
     */
    private void noticePlayersLandlordOwnershipResult(int landlordPosId, int landlordScore, Integer[] holeCards) {
        log.info("地主归属： posId={}", landlordPosId);
        LandlordOwnershipResponse response = new LandlordOwnershipResponse(landlordPosId, landlordScore, gameZone.getGameStatus().status());
        response.setHoleCards(Arrays.asList(holeCards));
        pushToRoomUser(PushCommandCode.LANDLORD_OWNERSHIP, response);

    }

    /**
     * 农民叫 加倍 优先于 地主叫加倍
     *
     * @param isFarmersFinishRedouble : 农民阵营是否已经完成加倍操作
     */
    private void noticePlayersRedoubleOption(boolean isFarmersFinishRedouble) {
        RedoubleOptionResponse redoubleOption = new RedoubleOptionResponse(Arrays.asList(gameZone.getRedoubleOption()));
        DouDiZhuSeat landlordSeat = posIdSeatMap.get(gameZone.getLandlordPosId());
        if (!isFarmersFinishRedouble) {
            /**
             * 刚完成叫分流程
             */
            noticePlayerCurrentOperator(getGameStatus().getTimeoutTime());

            pushToRoomUser(PushCommandCode.REDOUBLE_OPTION, redoubleOption, landlordSeat.getUserId());
            return;
        }
        noticePlayerCurrentOperator(getGameStatus().getTimeoutTime());

        IPushManager pushManager = roomManager.getPushManager();
        pushManager.pushToUser(PushCommandCode.REDOUBLE_OPTION, landlordSeat.getUserId(), redoubleOption, roomId);

    }

    /**
     * 通知玩家当前操作人是谁
     */
    private void noticePlayerCurrentOperator(int timeoutSeconds) {
        List<Integer> operatorList = gameZone.getCurrentNoticePosIds();
        IPushManager pushManager = roomManager.getPushManager();

        Long[] excludeUserIds = new Long[operatorList.size()];
        int i = 0;
        for (Integer posId : operatorList) {
            CurrentOperatorResponse response = new CurrentOperatorResponse(operatorList, timeoutSeconds);
            DouDiZhuSeat seat = posIdSeatMap.get(posId);
            pushManager.pushToUser(PushCommandCode.CURRENT_OPERATOR, seat.getUserId(), response, roomId);
            excludeUserIds[i] = seat.getUserId();
            i++;
        }
        CurrentOperatorResponse response = new CurrentOperatorResponse(operatorList, gameZone.getGameStatus().getTimeoutTime());
        pushToRoomUser(PushCommandCode.CURRENT_OPERATOR, response, excludeUserIds);

        /**
         * 如果获得主动出牌权的玩家还剩下一张牌，让其自动出牌
         */
        if(GameStatusEnum.OPERATION_CARD.equals(gameZone.getGameStatus())){
            Integer currentPosId = operatorList.get(0);
            DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(currentPosId);
            List<Integer> handCardList = douDiZhuSeat.getHandCardList();
            if(handCardList.size() == 1){
                int lastOutCardPosId = gameZone.getLastOutCardPosId();
                if(currentPosId == lastOutCardPosId){
                    ddzTimeoutTask = new DdzTimeoutTask(100, this);
                    timeoutTaskPool.addTask(ddzTimeoutTask);
                }
            }

        }
    }

    /**
     * 通知房间玩家  当前玩家的 加倍流程 操作的结果
     *
     * @param command
     * @param response
     */
    private void noticePlayersRedoubleResult(int command, Response response) {
        pushToRoomUser(command, response);
    }

    private void noticePlayersRedoubleDetail() {
        DouDiZhuSeat landLordSeat = posIdSeatMap.get(gameZone.getLandlordPosId());
        for (Map.Entry<Integer, DouDiZhuSeat> entry : posIdSeatMap.entrySet()) {
            DouDiZhuSeat douDiZhuSeat = entry.getValue();
            RedoubleDetailResponse redoubleDetail = gameZone.getRedoubleDetail(douDiZhuSeat, landLordSeat);
            IPushManager pushManager = roomManager.getPushManager();
            pushManager.pushToUser(PushCommandCode.REDOUBLE_DETAIL, douDiZhuSeat.getUserId(), redoubleDetail, roomId);
        }

    }

    private void noticePlayersOperationCardResult(Response response) {
        pushToRoomUser(PushCommandCode.OPEATION_CARD, response);
    }

    private void noticePlayersAlarm(int posId) {
        CardRemainingAlarmResponse response = new CardRemainingAlarmResponse(posId);
        pushToRoomUser(PushCommandCode.CARD_REMAINING_ALARM, response);
    }

    private void isSpringTime() {
        if (gameZone.isSpringTime()) {
            noticePlayersRedoubleDetail();
            noticePlayersSpringTime();
        }
    }

    private void noticePlayersSpringTime() {
        int who = gameZone.winnerCampisLandlord() ? CommonConstant.LANDLORD_SPRING_TIME : CommonConstant.FARMER_SPRING_TIME;
        SpringTimeResponse springTimeResponse = new SpringTimeResponse(gameZone.isSpringTime(), who);
        pushToRoomUser(PushCommandCode.SPRING_TIME, springTimeResponse);
    }

    private void noticePlayersSettlementResult() {
        List<SettlementStep> settlementStepList = gameZone.getSettlementStep();
        List<SettlementDTO> settlementDTOList = new ArrayList<>();
        settlementStepList.stream().forEach(settlementStep -> {
            SettlementDTO settlementDTO = new SettlementDTO();
            settlementDTO.setStep(settlementStep.getStep())
                    .setPosId(settlementStep.getPosId())
                    .setBaseScore(settlementStep.getBaseScore())
                    .setChangeScore(settlementStep.getScoreChange())
                    .setHandCards(settlementStep.getHandCards())
                    .setResultRedoubleNum(settlementStep.getResultRedoubleNum());
            settlementDTOList.add(settlementDTO);
        });
        SettlementResponse response = new SettlementResponse(settlementDTOList);
        pushToRoomUser(PushCommandCode.SETTLEMENT, response);
    }


    private void buildStep(Step step, int posId) {
        GameStepModel gameStepModel = new GameStepModel(gameZone.getZoneId(), posIdSeatMap.get(posId).getPlayer(), step);
        gameZone.addHistory(gameStepModel);
        gameZone.stepAdd();
    }

    public void pushToRoomUser(int command, Response response, Long... excludeUserIds) {
        IPushManager pushManager = roomManager.getPushManager();
        for (Long userId : userPosIdMap.keySet()) {
            boolean isSend = true;
            for (Long excludeUserId : excludeUserIds) {
                if (userId.equals(excludeUserId)) {
                    isSend = false;
                    break;
                }
            }

            if (isSend) {
                pushManager.pushToUser(command, userId, response, roomId);
            }

        }
    }

    private boolean isExistsAll(List<Integer> cardList, List<Integer> targetCardList) {
        List<Integer> list = new ArrayList<>(cardList);
        for (Integer target : targetCardList) {
            boolean remove = list.remove(target);
            if (!remove) {
                return false;
            }
        }
        return true;
    }

    //===================================================================


    @Override
    public DouDiZhuRoom cloneData() throws CloneNotSupportedException {
        return this.clone();
    }

    /**
     * 超时机制
     *
     * @return
     */
    public List<Integer> getTimeoutOutCard() {
        int currentOperatorPosId = gameZone.getCurrentOperatorPosId();
        List<Integer> cardList = new ArrayList<>();
        if (!gameZone.canPass(currentOperatorPosId)) {
            DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(currentOperatorPosId);
            List<Integer> handCardList = douDiZhuSeat.getHandCardList();
            int size = handCardList.size();
            if (size == 0) {
                return null;
            }
            Integer cardIndex = handCardList.get(size - 1);
            cardList.add(cardIndex);
        }
        return cardList;
    }


    //托管
    public void addTimeoutTask() {
        List<Integer> currentOperatorPosIds = getCurrentOperatorPosIds();
        currentOperatorPosIds.stream().forEach(posId -> {
            DouDiZhuSeat seat = posIdSeatMap.get(posId);
            if (seat.isAutoOperation()) {
                ddzTimeoutTask = new DdzTimeoutTask(5, this);
                timeoutTaskPool.addTask(ddzTimeoutTask);
            } else {
                ddzTimeoutTask = new DdzTimeoutTask(getGameStatus().getTimeoutTime(), this);
                timeoutTaskPool.addTask(ddzTimeoutTask);
            }
        });
    }

    /**
     * 操作彻底执行完成后，已经通知了下一个操作玩家要操作的情景下
     *
     * @return
     */
    private List<Integer> getCurrentOperatorPosIds() {
        List<Integer> currentOperatorPosIds = new ArrayList<>();
        if (GameStatusEnum.FARMERS_REDOUBLE.equals(getGameStatus()) || GameStatusEnum.LANDOWNERS_REDOUBLE.equals(getGameStatus())) {
            currentOperatorPosIds = gameZone.getCurrentNoticePosIds();
            Iterator<Integer> iterator = currentOperatorPosIds.iterator();
            while (iterator.hasNext()) {
                Integer posId = iterator.next();
                if (posIdSeatMap.get(posId).getRedoubleNum().orElse(null) != null) {
                    iterator.remove();
                }
            }
        } else {
            currentOperatorPosIds.add(gameZone.getCurrentOperatorPosId());
        }
        return currentOperatorPosIds;
    }

    public List<Long> getCurrentOperatorUserIds() {
        List<Integer> currentOperatorPosIds = getCurrentOperatorPosIds();
        log.info("currentNoticePosIds :{}", currentOperatorPosIds);
        List<Long> userIds = new ArrayList<>();
        if (currentOperatorPosIds.size() > 1) {
            Iterator<Integer> iterator = currentOperatorPosIds.iterator();
            while (iterator.hasNext()) {
                Integer posId = iterator.next();
                DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(posId);
                if (douDiZhuSeat.isFinishRedouble()) {
                    iterator.remove();
                    continue;
                }
                userIds.add(douDiZhuSeat.getUserId());
            }
            log.info("userIds={}", userIds);
            return userIds;
        }
        DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(currentOperatorPosIds.get(0));
        userIds.add(douDiZhuSeat.getUserId());
        log.info("userIds={}", userIds);
        return userIds;
    }

    public DouDiZhuZone getDouDiZhuZone() {
        return gameZone;
    }



    public Integer[] getRedoubleOption() {
        return gameZone.getRedoubleOption();
    }

    public List<Integer> getCallScoreOption() {
        return gameZone.getCallScoreOption();
    }

    //=====================================================================================

    @Override
    public int getTimeoutLimit() {
        return RuleConfig.SERIAL_TIMEOUT_OUNT;
    }

    @Override
    public void init(IRoomManager roomManager, Long roomId, List list, int roundLimit, int inningLimit) {
        super.init(roomManager, roomId, list, roundLimit, inningLimit);
        List<Integer> cardDeck = gameZone.getCardDeck();
        //存储到分布式缓存中
    }

    @Override
    public void clean() {
        for (Map.Entry<Long, Integer> entry : userPosIdMap.entrySet()) {
            Integer posId = entry.getValue();
            DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(posId);
            Player player = douDiZhuSeat.getPlayer();
            roomManager.changePlayerStatus(player, PlayerStatusEnum.FREE_TIME);
        }
        destroy();
    }

    public static final AtomicInteger roomCount = new AtomicInteger(0);
    @Override
    public void destroy() {
        log.debug("完成第 {} 局游戏",roomCount.incrementAndGet());
        int gameInning = gameZone.getInning();
        boolean isFinish = gameInning >= inningLimit;
        if (isFinish) {
            log.info("房间销毁： roomId={} ", roomId);
            //房间销毁
            posIdSeatMap = null;
            roomManager.destroyRoom(roomId);
        }
    }




    public int getSeatStatus(Long userId) {
        Integer posId = userPosIdMap.get(userId);
        DouDiZhuSeat douDiZhuSeat = posIdSeatMap.get(posId);
        int status = douDiZhuSeat.getPlayer().getStatus().status();
        return status;
    }




    @Override
    public DouDiZhuRoom clone() throws CloneNotSupportedException {
        DouDiZhuRoom cloneRoom = (DouDiZhuRoom) super.clone();
        cloneRoom.gameZone = (DouDiZhuZone) cloneRoom.gameZone.clone();
        Map<Integer, DouDiZhuSeat> seatMap = new HashMap<>();
        for (Map.Entry<Integer, DouDiZhuSeat> entry : cloneRoom.posIdSeatMap.entrySet()) {
            Integer posId = entry.getKey();
            DouDiZhuSeat seat = entry.getValue();
            seatMap.put(posId, (DouDiZhuSeat) seat.clone());
        }
        cloneRoom.posIdSeatMap = seatMap;
        return cloneRoom;
    }

    @Override
    public String toString() {
        return "DouDiZhuRoom{" +
                "gameZone=" + gameZone +
                ", roomId=" + roomId +
                ", roomManager=" + roomManager +
                ", roundLimit=" + roundLimit +
                ", inningLimit=" + inningLimit +
                ", userPosIdMap=" + userPosIdMap +
                ", posIdSeatMap=" + posIdSeatMap +
                '}';
    }
}
