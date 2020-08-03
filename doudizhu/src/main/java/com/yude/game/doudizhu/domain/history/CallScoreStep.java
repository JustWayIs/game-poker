package com.yude.game.doudizhu.domain.history;

import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.doudizhu.domain.card.PokerProp;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/6 21:28
 * @Version: 1.0
 * @Declare:
 */
public class CallScoreStep implements Step{
    private final int step;
    private final int posId;
    private final int score;
    private final List<Integer> handCards;
    private final List<PokerProp.CardEunm> handCardsEnum;
    private final GameStatusEnum type;

    public CallScoreStep(int step,int posId, int score,List<Integer> handCards,GameStatusEnum type) {
        this.step = step;
        this.posId = posId;
        this.score = score;
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

    public int getScore() {
        return score;
    }

    public GameStatusEnum getType() {
        return type;
    }


    public List<Integer> getHandCards() {
        return handCards;
    }

    public List<PokerProp.CardEunm> getHandCardsEnum() {
        return handCardsEnum;
    }

    @Override
    public String toString() {
        return "CallScoreStep{" +
                "step=" + step +
                ", posId=" + posId +
                ", score=" + score +
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
