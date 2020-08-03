package com.yude.game.doudizhu.domain.card;



import java.util.Arrays;

/**
 * @Author: HH
 * @Date: 2020/7/6 16:22
 * @Version: 1.0
 * @Declare:
 */
public class Card {
    private  Integer[] cards;
    /**
     * 传给客户端的时候，需要转一下
     */
    private CardTypeInfo cardType;

    public Card() {
    }

    public Card(Integer[] cards, CardTypeInfo cardType) {
        this.cards = cards;
        this.cardType = cardType;
    }

    public Integer[] getCards() {
        return cards;
    }

    public Card setCards(Integer[] cards) {
        this.cards = cards;
        return this;
    }

    public CardTypeInfo getCardType() {
        return cardType;
    }

    public Card setCardType(CardTypeInfo cardType) {
        this.cardType = cardType;
        return this;
    }

    @Override
    public String toString() {
        return "Card{" +
                "cards=" + Arrays.toString(cards) +
                ", cardType=" + cardType +
                '}';
    }
}
