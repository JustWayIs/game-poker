package com.yude.game.doudizhu.domain.history;

import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.doudizhu.domain.card.PokerProp;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/6 21:29
 * @Version: 1.0
 * @Declare:
 */
public class RedoubleStep implements Step{
    private final int step;
    private final int posId;
    private final int redoubleNum;
    private final List<Integer> handCards;
    private final List<PokerProp.CardEunm> handCardsEnum;
    private final GameStatusEnum type;

    public RedoubleStep(int step, int posId, int redoubleNum,List<Integer> handCards,GameStatusEnum type) {
        this.step = step;
        this.posId = posId;
        this.redoubleNum = redoubleNum;
        this.type = type;
        this.handCards = handCards;
        this.handCardsEnum = PokerProp.convertCardForNum(handCards);
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
        return null;
    }

    public int getRedoubleNum() {
        return redoubleNum;
    }


    public List<Integer> getHandCards() {
        return handCards;
    }

    public List<PokerProp.CardEunm> getHandCardsEnum() {
        return handCardsEnum;
    }

    public GameStatusEnum getType() {
        return type;
    }

    @Override
    public String toString() {
        return "RedoubleStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", redoubleNum=" + redoubleNum +
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
