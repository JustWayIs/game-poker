package com.yude.game.doudizhu.domain.history;

import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.card.Card;
import com.yude.game.doudizhu.domain.card.PokerProp;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/6 21:12
 * @Version: 1.0
 * @Declare:
 */
public class GameStartStep implements Step {
    private long roomId;
    private int zoneId;
    private int step;
    private int posId;
    private List<Integer> handCards;
    private List<PokerProp.CardEunm> handCardsEnum;
    private GameStatusEnum type;


    public long getRoomId() {
        return roomId;
    }

    public GameStartStep setRoomId(long roomId) {
        this.roomId = roomId;
        return this;
    }

    public int getZoneId() {
        return zoneId;
    }

    public GameStartStep setZoneId(int zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public int getStep() {
        return step;
    }

    public GameStartStep setStep(int step) {
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

    public GameStartStep setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public GameStartStep setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
        this.handCardsEnum = PokerProp.convertCardForNum(handCards);
        return this;
    }

    public GameStatusEnum getType() {
        return type;
    }

    public GameStartStep setType(GameStatusEnum type) {
        this.type = type;
        return this;
    }

    public List<PokerProp.CardEunm> getHandCardsEnum() {
        return handCardsEnum;
    }

    public GameStartStep setHandCardsEnum(List<PokerProp.CardEunm> handCardsEnum) {
        this.handCardsEnum = handCardsEnum;
        return this;
    }

    @Override
    public String toString() {
        return "GameStartStep{" +
                "roomId=" + roomId +
                ", zoneId=" + zoneId +
                ", step=" + step +
                ", posId=" + posId +
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
