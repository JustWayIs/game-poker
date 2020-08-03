package com.yude.game.doudizhu.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/13 16:41
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class CardDTO {
    private List<Integer> cards;
    private Integer cardType;

    public CardDTO() {
        cards = new ArrayList<>();
    }

    public CardDTO(List<Integer> cards, Integer cardType) {
        this.cards = cards;
        this.cardType = cardType;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public CardDTO setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public Integer getCardType() {
        return cardType;
    }

    public CardDTO setCardType(Integer cardType) {
        this.cardType = cardType;
        return this;
    }

    @Override
    public String toString() {
        return "CardDTO{" +
                "cards=" + cards +
                ", cardType=" + cardType +
                '}';
    }
}
