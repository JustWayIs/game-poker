package com.yude.game.doudizhu.domain;

import com.yude.game.common.constant.Status;
import com.yude.game.common.model.AbstractSeatModel;
import com.yude.game.common.model.Player;
import com.yude.game.doudizhu.application.response.dto.CardDTO;
import com.yude.game.doudizhu.constant.status.SeatStatusEnum;
import com.yude.game.doudizhu.domain.card.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author: HH
 * @Date: 2020/6/29 15:41
 * @Version: 1.0
 * @Declare:
 */
public class DouDiZhuSeat extends AbstractSeatModel implements Cloneable {
    public static final Logger log = LoggerFactory.getLogger(DouDiZhuSeat.class);


    /**
     * 可以出的牌
     */
    private List<Card> operationCardList;

    private List<Integer> handCardList;

    /**
     * 叫分： 有为null的需要
     */
    private Integer callScore;

    /**
     * 加倍： 有为null的需要
     */
    private Integer redoubleNum;

    private Status status;


    private List<CardDTO> tips;

    /**
     * 是否已经报警过，只报警1次
     */
    private boolean canAlarm = true;

    public DouDiZhuSeat(Player player, int posId) {
        super(player, posId);
        operationCardList = new ArrayList<>();
    }

    @Override
    public void init() {

    }

    @Override
    public void clean() {
        operationCardList = null;
        callScore = null;
        redoubleNum = null;
        status = null;
        tips = null;
    }

    public void reset() {
        operationCardList = new ArrayList<>();
        callScore = null;
        redoubleNum = null;
        status = SeatStatusEnum.DEAL_CARD;
        isAutoOperation = false;
        serialTimeoutCount = 0;
        canAlarm = true;
    }

    public boolean isFinishRedouble() {
        return status.status() > SeatStatusEnum.REDOUBLE.status();
    }

    public List<Card> getOperationCardList() {
        return operationCardList;
    }

    public void setOperationCardList(List<Card> operationCardList) {
        this.operationCardList = operationCardList;
    }

    public List<Integer> setHandCardList(List<Integer> handCardList) {
        this.handCardList = handCardList;
        return this.handCardList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Optional<Integer> getCallScore() {
        return Optional.ofNullable(callScore);
    }

    public void setCallScore(Integer callScore) {
        this.callScore = callScore;
    }

    public Optional<Integer> getRedoubleNum() {
        return Optional.ofNullable(redoubleNum);
    }

    public int getRedoubleDetailShowRedoubleForLandlord() {
        Optional<Integer> redoubleNum = Optional.ofNullable(this.redoubleNum);
        Integer showRedoubleValue = redoubleNum.orElse(1);
        showRedoubleValue = showRedoubleValue == 1 ? 0 : showRedoubleValue;
        return showRedoubleValue;
    }

    public int getRedoubleDetailShowRedoubleForFarmer() {
        Optional<Integer> redoubleNum = Optional.ofNullable(this.redoubleNum);
        Integer showRedoubleValue = redoubleNum.orElse(1);
        return showRedoubleValue;
    }

    public void setRedoubleNum(Integer redoubleNum) {
        this.redoubleNum = redoubleNum;
    }

    public List<Integer> getHandCardList() {
        return handCardList;
    }


    public void setOutCardTips(List<CardDTO> outCardTips) {
        tips = outCardTips;
    }

    public List<CardDTO> getTips() {
        return tips;
    }

    public boolean isCanAlarm() {
        return canAlarm;
    }

    public DouDiZhuSeat setCanAlarm(boolean canAlarm) {
        this.canAlarm = canAlarm;
        return this;
    }

    @Override
    protected DouDiZhuSeat clone() throws CloneNotSupportedException {
        return (DouDiZhuSeat) super.clone();
    }

    @Override
    public String toString() {
        return "DouDiZhuSeat{" +
                "operationCardList=" + operationCardList +
                ", handCardList=" + handCardList +
                ", callScore=" + callScore +
                ", redoubleNum=" + redoubleNum +
                ", status=" + status +
                ", isAutoOperation=" + isAutoOperation +
                ", player=" + player +
                ", posId=" + posId +
                '}';
    }
}
