package com.yude.game.doudizhu.domain.history;

import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.doudizhu.domain.card.PokerProp;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/9 21:04
 * @Version: 1.0
 * @Declare:
 */
public class SettlementStep implements Step {

    private int step;
    private int posId;
    private int baseScore;
    private int scoreChange;
    private int resultRedoubleNum;
    private List<Integer> handCards;
    private List<PokerProp.CardEunm> handCardsEnum;
    private GameStatusEnum type;

    public int getStep() {
        return step;
    }

    public SettlementStep setStep(int step) {
        this.step = step;
        return this;
    }

    @Override
    public int getPosId() {
        return posId;
    }

    @Override
    public Card getCard() {
        return null;
    }

    public SettlementStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public SettlementStep setBaseScore(int baseScore) {
        this.baseScore = baseScore;
        return this;
    }

    public int getScoreChange() {
        return scoreChange;
    }

    public SettlementStep setScoreChange(int scoreChange) {
        this.scoreChange = scoreChange;
        return this;
    }

    public int getResultRedoubleNum() {
        return resultRedoubleNum;
    }

    public SettlementStep setResultRedoubleNum(int resultRedoubleNum) {
        this.resultRedoubleNum = resultRedoubleNum;
        return this;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public SettlementStep setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
        handCardsEnum = PokerProp.convertCardForNum(handCards);
        return this;
    }

    public List<PokerProp.CardEunm> getHandCardsEnum() {
        return handCardsEnum;
    }

    public SettlementStep setHandCardsEnum(List<PokerProp.CardEunm> handCardsEnum) {
        this.handCardsEnum = handCardsEnum;
        return this;
    }

    public GameStatusEnum getType() {
        return type;
    }

    public SettlementStep setType(GameStatusEnum type) {
        this.type = type;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", baseScore=" + baseScore +
                ", scoreChange=" + scoreChange +
                ", resultRedoubleNum=" + resultRedoubleNum +
                ", handCards=" + handCards +
                ", handCardsEnum=" + handCardsEnum +
                ", type=" + type +
                '}';
    }

    @Override
    public GameStatusEnum stepType() {
        return type;
    }
}
