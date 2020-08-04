package com.yude.game.doudizhu.domain;

import com.yude.game.common.model.AbstractGameZoneModel;
import com.yude.game.doudizhu.constant.RuleConfig;
import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.constant.status.SeatStatusEnum;
import com.yude.game.doudizhu.domain.action.BaseGameProcess;
import com.yude.game.doudizhu.domain.card.CardType;
import com.yude.game.doudizhu.domain.card.CardTypeEnum;
import com.yude.game.doudizhu.domain.card.PokerProp;
import com.yude.game.doudizhu.domain.history.*;
import com.yude.game.doudizhu.application.response.RedoubleDetailResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.yude.game.doudizhu.domain.card.Card;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: HH
 * @Date: 2020/6/29 15:41
 * @Version: 1.0
 * @Declare:
 */
public class DouDiZhuZone extends AbstractGameZoneModel<DouDiZhuSeat,GameStatusEnum> implements BaseGameProcess, Cloneable {
    public static final Logger log = LoggerFactory.getLogger(DouDiZhuZone.class);

    /**
     * 初始化完成后，存储的应该是已经打乱顺序后的牌组。
     */
    protected List<Integer> cardDeck;
    /**
     * 底牌
     */
    private Integer[] holeCards;
    private List<GameStepModel> operationHistoryList;

    /**
     * H2 这些应该被视为游戏域的内容还是游戏房间的内容呢
     */
    private Integer[] callScoreOption = RuleConfig.callScoreOption;

    private Integer[] redoubleOption = RuleConfig.redoubleOption;

    //H2 底注
    private Integer baseScore = RuleConfig.baseScoreFactor;
    /**
     * 当前操作玩家
     */
    private volatile int currentOperatorPosId = -1;

    /**
     * 叫分玩家数量
     */
    private int finishCallScoreCount;
    /**
     * 用于和当前叫分的玩家 比较分数大小
     */
    private int beforeCallScore = -1;

    /**
     * 成为地主的分数
     */
    private int landlordScore;

    private int landlordPosId = -1;

    /**
     *
     */
    private int finishRedoubleCount;
    private int beforeRedoubleValue = -1;

    /**
     * 用于和当前出牌玩家 比较牌的的大小
     */
    private Card lastOutCard;
    private volatile int lastOutCardPosId = -1;

    /**
     * 把牌出完的玩家
     */
    private Integer winnerPosId;

    /**
     * 炸弹数量
     */
    private int bombCount;

    /**
     * 是否有春天
     */
    private boolean springTime;

    /**
     * 超时机制
     */
    private volatile long lastOperationTime;

    /**
     * 是否为重开局（都不叫分）的标识
     */
    private boolean reStartGame;


    public DouDiZhuZone(DouDiZhuSeat[] seats, int round, int inning) {
        super(seats, round, inning);
        holeCards = new Integer[3];
        stepCount = 0;
        operationHistoryList = new ArrayList<>();
    }

    @Override
    public void init() {
        cardDeck = new ArrayList<>(PokerProp.CARDS);
        Collections.shuffle(cardDeck);
        gameStatus = GameStatusEnum.DEAL_CARD;

    }

    @Override
    public void clean() {
        // 还有一个结算在当前没有加进来 log.info("当局的过程：{}",operationHistoryList);
        playerSeats = null;
    }

    @Override
    public void start(Long roomId) {
        deal(roomId);
        //0号玩家在该游戏没有任何操作的情况下重连了
        lastOperationTime = System.currentTimeMillis();
        gameStatus = GameStatusEnum.CALL_SCORE;
    }

    @Override
    public Integer callScore(int posId, int score) {
        //当玩家不叫分的时候，存的应该是之前的玩家叫的分，方便做比较。只有当第一个玩家不叫的时候，可以设置为0分
        if (score > 0 || beforeCallScore == -1) {
            beforeCallScore = score;
        }
        finishCallScoreCount++;
        if (score == getMaxScore()) {
            becomeLandlord(posId, score);
            return posId;
        }

        if (finishCallScoreCount == playerSeats.length) {
            gameStatus = GameStatusEnum.FARMERS_REDOUBLE;
            DouDiZhuSeat landlordSeat = playerSeats[0];
            for (DouDiZhuSeat seatModel : playerSeats) {
                if (landlordSeat.getCallScore().get() < seatModel.getCallScore().get()) {
                    landlordSeat = seatModel;
                }
            }
            becomeLandlord(landlordSeat.getPosId(), landlordSeat.getCallScore().get());
            return landlordPosId;
        }
        lastOperationTime = System.currentTimeMillis();
        return null;
    }

    public Integer getMaxScore() {
        return callScoreOption[callScoreOption.length - 1];
    }

    public void becomeLandlordForcedFirstPlayer() {
        //不太健康的写法:注意 强行指定地主时，并没有修改地主位置的叫分数值，只是改了游戏的地主分数值
        DouDiZhuSeat playerSeat = playerSeats[0];
        landlordScore = 1;
        landlordPosId = playerSeat.getPosId();
        becomeLandlord(landlordPosId, landlordScore);
    }

    public void becomeLandlord(int posId, int score) {
        this.landlordPosId = posId;
        this.landlordScore = score;
        gameStatus = GameStatusEnum.FARMERS_REDOUBLE;
        lastOperationTime = System.currentTimeMillis();
    }

    @Override
    public boolean farmersRedouble(int redoubleValue) {
        finishRedoubleCount++;
        beforeRedoubleValue = redoubleValue > beforeRedoubleValue ? redoubleValue : beforeRedoubleValue;
        if (finishRedoubleCount == (playerSeats.length - 1)) {
            gameStatus = GameStatusEnum.LANDOWNERS_REDOUBLE;
            if (beforeRedoubleValue == redoubleOption[0]) {
                gameStatus = GameStatusEnum.OPERATION_CARD;
            }
            lastOperationTime = System.currentTimeMillis();
            return true;
        }
        lastOperationTime = System.currentTimeMillis();
        return false;
    }

    public boolean isNeedLandlordRedoubleOperation() {
        log.info("是否需要地主进行加倍操作： beforeRedoubleValue={}", beforeRedoubleValue);
        boolean isNeed = beforeRedoubleValue > redoubleOption[0];
        if (!isNeed) {
            operationCardReady();
        }
        return isNeed;
    }

    private void operationCardReady() {
        gameStatus = GameStatusEnum.OPERATION_CARD;
        int beforeLandlordPosId = landlordPosId - 1;
        if (beforeLandlordPosId < 0) {
            beforeLandlordPosId = playerSeats.length - 1;
        }
        //这样的话，到行牌阶段，获取的nextPosId就是地主
        currentOperatorPosId = beforeLandlordPosId;
    }

    @Override
    public boolean landownersRedouble() {
        finishRedoubleCount++;
        if (finishRedoubleCount == playerSeats.length) {
            operationCardReady();
            lastOperationTime = System.currentTimeMillis();
            return true;
        }
        lastOperationTime = System.currentTimeMillis();
        return false;
    }


    @Override
    public boolean operationCard(int posId, Card card) {
        lastOperationTime = System.currentTimeMillis();
        if (card == null) {
            return false;
        }
        lastOutCard = card;
        lastOutCardPosId = posId;
        if (CardTypeEnum.炸弹.equals(card.getCardType().type()) || CardTypeEnum.王炸.equals(card.getCardType().type())) {
            bombCountAdd();
            return true;
        }
        return false;
    }

    public void bombCountAdd() {
        bombCount++;
    }


    public int getBombRedoubleValue() {
        return bombCount * 2;
    }

    public int getBombRedoubleShowValue() {
        int value = bombCount * 2;
        value = value == 0 ? 1 : value;
        return value;
    }

    @Override
    public void gameOver(int winnerPosId) {
        this.winnerPosId = winnerPosId;
        gameStatus = GameStatusEnum.SETTLEMENT;
        settlement();
        //clean();
    }

    @Override
    public void settlement() {
        judgeSpringTime();
    }

    public void judgeSpringTime() {
        Map<Integer, Long> posIdAndStepNumMap = operationHistoryList.stream().filter(gameStepModel -> gameStepModel.getOperationStep().stepType().status() == GameStatusEnum.OPERATION_CARD.status()).filter(gameStepModel -> gameStepModel.getOperationStep().getCard() != null).map(gameStepModel -> gameStepModel.getOperationStep().getPosId()).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        if (posIdAndStepNumMap.size() == 1) {
            //只有地主出过牌
            springTime = true;
            return;
        }
        Long landlordOperationCardStepNum = posIdAndStepNumMap.get(landlordPosId);
        if (landlordOperationCardStepNum == 1) {
            //反春天
            springTime = true;
            return;
        }
    }

    /**
     * 每次倍数变动的时候（从地主）都要发一次
     */
    public RedoubleDetailResponse getRedoubleDetail(DouDiZhuSeat currentSeat, DouDiZhuSeat landlordSeat) {
        if (landlordSeat == null) {
            return null;
        }
        RedoubleDetailResponse response = new RedoubleDetailResponse();
        //H2 初始分：不是叫的分，类似于房间底注
        response.setBaseScore(baseScore)
                .setBombCount(getBombRedoubleValue())
                .setSpringTimeCount(getSpringTimeRedoubleValue())
                .setCommonRedoubleNum(getCommonRedoubleNum() * response.getBaseScore());


        int farmerRedoubleNum = 0;
        int landlordRedoubleNum = 0;
        if (currentSeat.equals(landlordSeat)) {
            for (DouDiZhuSeat seat : playerSeats) {
                if (seat.getPosId() != currentSeat.getPosId()) {
                    farmerRedoubleNum += seat.getRedoubleDetailShowRedoubleForLandlord();
                }
            }
            farmerRedoubleNum = farmerRedoubleNum == 0 ? 1 : farmerRedoubleNum;
            landlordRedoubleNum = landlordSeat.getRedoubleDetailShowRedoubleForFarmer();
        } else {

            farmerRedoubleNum = currentSeat.getRedoubleDetailShowRedoubleForFarmer();
            landlordRedoubleNum = farmerRedoubleNum == 1 ? farmerRedoubleNum : landlordSeat.getRedoubleDetailShowRedoubleForFarmer();
        }
        response.setLandlordRedoubleNum(landlordRedoubleNum)
                .setFarmerRedoubleNum(farmerRedoubleNum)
                .setResultRedoubleNum(response.getCommonRedoubleNum() * response.getFarmerRedoubleNum() * response.getLandlordRedoubleNum());
        return response;

    }

    public boolean isSpringTime() {
        return springTime;
    }

    public int getSpringTimeRedoubleValue() {
        return springTime ? 2 : 0;
    }

    public int getCommonRedoubleNum() {
        return springTime ? 2 * getBombRedoubleShowValue() : 1 * getBombRedoubleShowValue();
    }

    public int getResultRedoubleValueToLandlord(DouDiZhuSeat landlordSeat) {
        return getCommonRedoubleNum();
    }

    public boolean winnerCampisLandlord() {
        return winnerPosId == landlordPosId;
    }

    void deal(Long roomId) {
        for (DouDiZhuSeat seat : playerSeats) {
            int i = 0;


            List<Integer> subCardList = cardDeck.subList(i, i + 17);

            /**
             * 用数组，是代表着这个牌不再变化，使语义更明确 ，可是转来转去反而也麻烦
             */
            /*Integer[] cardIndexs = new Integer[subCardList.size()];
            subCardList.toArray(cardIndexs);*/
            List<Integer> stepCardIndexs = new ArrayList<>(subCardList);

            List<Integer> cardList = seat.setHandCardList(new ArrayList<>(subCardList));
            CardType.sort(cardList);

            GameStartStep gameStartStep = new GameStartStep();
            gameStartStep.setRoomId(roomId)
                    .setZoneId(getZoneId())
                    .setStep(getStepCount())
                    .setPosId(seat.getPosId())
                    .setHandCards(stepCardIndexs)
                    .setType(getGameStatus());
            GameStepModel model = new GameStepModel(getZoneId(), seat.getPlayer(), gameStartStep);
            getOperationHistoryList().add(model);

            //如果要记牌器功能的话，就不应该清除，要在出牌的时候清除，或者再加一个专门用于记牌器的属性
            subCardList.clear();
            seat.setStatus(SeatStatusEnum.CALL_SCORE);

        }
        cardDeck.toArray(getHoleCards());
        //发牌应该是3步 还是1步
        stepAdd();

    }

    public List<Integer> getCardDeck() {
        return cardDeck;
    }

    public List<GameStepModel> getOperationHistoryList() {
        return operationHistoryList;
    }

    public void addHistory(GameStepModel gameStepModel) {
        operationHistoryList.add(gameStepModel);
    }

    public int getBeforeCallScore() {
        return beforeCallScore;
    }

    public void stepAdd() {
        stepCount++;
    }

    public Integer[] getHoleCards() {
        return holeCards;
    }


    @Override
    public GameStatusEnum getGameStatus() {
        return gameStatus;
    }

    public int getFinishCallScoreCount() {
        return finishCallScoreCount;
    }

    public int getLandlordPosId() {
        return landlordPosId;
    }

    public int getFinishRedoubleCount() {
        return finishRedoubleCount;
    }

    public int getCurrentOperatorPosId() {
        return currentOperatorPosId;
    }

    public int getBeforeOperatorPosId() {
        int beforeOperatorPosId = currentOperatorPosId - 1;
        if (beforeOperatorPosId < 0) {
            beforeOperatorPosId = playerSeats.length - 1;
        }
        return beforeOperatorPosId;
    }

    public List<Integer> getCurrentNoticePosIds() {
        List<Integer> operatorList = new ArrayList<>();
        if (GameStatusEnum.FARMERS_REDOUBLE.equals(getGameStatus())) {
            for (DouDiZhuSeat seat : playerSeats) {
                if (seat.getPosId() == getLandlordPosId()) {
                    continue;
                }
                operatorList.add(seat.getPosId());
            }

        } else if (GameStatusEnum.LANDOWNERS_REDOUBLE.equals(getGameStatus())) {
            //由于 通知玩家操作 那里已经nextPosId了，所以这里不能再nextPosId;
            operatorList.add(landlordPosId);
        } else {
            //注意：这里被重复调用就会是操作人混乱
            operatorList.add(nextOperatorPosId());

        }
        return operatorList;
    }

    public Card getLastOutCard() {
        return lastOutCard;
    }

    public List<Integer> getCallScoreOption() {
        int beforeCallScore = getBeforeCallScore();
        List<Integer> callScoreOptionList = new ArrayList<>();
        if (beforeCallScore != -1) {
            //保证第一个值是不叫分
            callScoreOptionList.add(callScoreOption[0]);
        }
        for (Integer option : callScoreOption) {
            if (option > beforeCallScore) {
                callScoreOptionList.add(option);
            }

        }
        return callScoreOptionList;
    }

    public Integer[] getRedoubleOption() {
        return redoubleOption;
    }

    public Integer getWinnerPosId() {
        return winnerPosId;
    }

    public Integer getBaseScore() {
        return baseScore;
    }

    public int getLandlordScore() {
        return landlordScore;
    }

    public int getLastOutCardPosId() {
        return lastOutCardPosId;
    }

    public long getLastOperationTime() {
        return lastOperationTime;
    }

    public int getNextPosIdInfo() {
        int next = currentOperatorPosId + 1;
        if (next >= playerSeats.length) {
            next = 0;
        }
        return next;
    }

    public int nextOperatorPosId() {
        currentOperatorPosId++;
        if (currentOperatorPosId >= playerSeats.length) {
            currentOperatorPosId = 0;
        }
        return currentOperatorPosId;
    }

    public List<SettlementStep> getSettlementStep() {
        List<SettlementStep> settlementStepList = new ArrayList<>();
        for (GameStepModel gameStepModel : operationHistoryList) {
            Step step = gameStepModel.getOperationStep();
            if (GameStatusEnum.SETTLEMENT.equals(step.stepType())) {
                settlementStepList.add((SettlementStep) step);
            }
        }
        return settlementStepList;
    }

    public List<OperationCardStep> getOperationCardStep() {
        List<OperationCardStep> operationCardStepList = new ArrayList<>();
        for (GameStepModel gameStepModel : operationHistoryList) {
            Step step = gameStepModel.getOperationStep();
            if (GameStatusEnum.OPERATION_CARD.equals(step.stepType())) {
                operationCardStepList.add((OperationCardStep) step);
            }
        }
        return operationCardStepList;
    }

    public boolean canPass(int posId) {
        if (posId == getLastOutCardPosId()) {
            return false;
        }

        if (posId == landlordPosId && getOperationCardStep().size() == 0) {
            return false;
        }
        return true;
    }

    public boolean isReStartGame() {
        return reStartGame;
    }

    public void setReStartGame() {
        reStartGame = true;
    }

    @Override
    protected DouDiZhuZone clone() throws CloneNotSupportedException {
        DouDiZhuZone cloneZone = (DouDiZhuZone) super.clone();
        int i = 0;
        cloneZone.playerSeats = cloneZone.playerSeats.clone();
        for (DouDiZhuSeat seat : cloneZone.playerSeats) {
            cloneZone.playerSeats[i] = (DouDiZhuSeat) seat.clone();
            i++;
        }
        return cloneZone;
    }

    @Override
    public String toString() {
        return "DouDiZhuZone{" +
                "holeCards=" + Arrays.toString(holeCards) +
                ", operationHistoryList=" + operationHistoryList +
                ", gameStatus=" + gameStatus +
                ", callScoreOption=" + Arrays.toString(callScoreOption) +
                ", redoubleOption=" + Arrays.toString(redoubleOption) +
                ", currentOperatorPosId=" + currentOperatorPosId +
                ", finishCallScoreCount=" + finishCallScoreCount +
                ", beforeCallScore=" + beforeCallScore +
                ", landlordScore=" + landlordScore +
                ", landlordPosId=" + landlordPosId +
                ", finishRedoubleCount=" + finishRedoubleCount +
                ", beforeRedoubleValue=" + beforeRedoubleValue +
                ", lastOutCard=" + lastOutCard +
                ", lastOutCardPosId=" + lastOutCardPosId +
                ", winnerPosId=" + winnerPosId +
                ", bombCount=" + bombCount +
                ", springTime=" + springTime +
                ", lastOperationTime=" + lastOperationTime +
                ", cardDeck=" + cardDeck +
                ", playerSeats=" + Arrays.toString(playerSeats) +
                ", zoneId=" + zoneId +
                ", round=" + round +
                ", inning=" + inning +
                ", stepCount=" + stepCount +
                '}';
    }
}
