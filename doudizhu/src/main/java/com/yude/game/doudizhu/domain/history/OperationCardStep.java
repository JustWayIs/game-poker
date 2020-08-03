package com.yude.game.doudizhu.domain.history;

import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.doudizhu.domain.card.PokerProp;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/6 15:21
 * @Version: 1.0
 * @Declare:
 */
public class OperationCardStep implements Step{
    private final int step;
    private final int posId;
    private Card card;
    private final int remainingCardSize;
    private final List<Integer> handCards;
    private final List<PokerProp.CardEunm> handCardsEnum;
    private final GameStatusEnum type ;

    /**
     * 过牌
     * @param step
     * @param posId
     */
    public OperationCardStep(int step, int posId,int remainingCardSize,List<Integer> handCards,GameStatusEnum type) {
        this.step = step;
        this.posId = posId;
        this.remainingCardSize = remainingCardSize;
        this.type = type;
        this.handCards = handCards;
        this.handCardsEnum = PokerProp.convertCardForNum(handCards);
    }

    /**
     * 正常出牌
     * @param step
     * @param posId
     * @param card
     */
    public OperationCardStep(int step, int posId, Card card,int remainingCardSize,List<Integer> handCards,GameStatusEnum type) {
        this.step = step;
        this.posId = posId;
        this.card = card;
        this.remainingCardSize = remainingCardSize;
        this.handCards = handCards;
        this.handCardsEnum = PokerProp.convertCardForNum(handCards);
        this.type = type;
    }

    public int getStep() {
        return step;
    }

    @Override
    public int getPosId() {
        return posId;
    }

    @Override
    public Card getCard() {
        return card;
    }

    public int getRemainingCardSize() {
        return remainingCardSize;
    }

    public GameStatusEnum getType() {
        return type;
    }


    @Override
    public GameStatusEnum stepType() {
        return type;
    }

    public OperationCardStep setCard(Card card) {
        this.card = card;
        return this;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public List<PokerProp.CardEunm> getHandCardsEnum() {
        return handCardsEnum;
    }

    @Override
    public String toString() {
        return "OperationCardStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", card=" + card +
                ", remainingCardSize=" + remainingCardSize +
                ", handCards=" + handCards +
                ", handCardsEnum=" + handCardsEnum +
                ", type=" + type +
                '}';
    }
}
