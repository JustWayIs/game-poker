package com.yude.game.doudizhu.application.response;

import com.yude.game.doudizhu.application.response.dto.CardDTO;
import com.yude.protocol.common.response.BaseResponse;


/**
 * @Author: HH
 * @Date: 2020/6/22 17:58
 * @Version: 1.0
 * @Declare:
 */

public class OperationCardResponse extends BaseResponse {
    private Integer step;
    private Integer posId;
    private CardDTO cards;
    private Integer remainingCardSize;



    public OperationCardResponse() {
    }

    /**
     * 不要、过 操作的响应
     * @param step
     * @param posId
     */
    public OperationCardResponse(int step, int posId,int remainingCardSize) {
        this.step = step;
        this.posId = posId;
        this.remainingCardSize = remainingCardSize;
        cards = new CardDTO();

    }

    /**
     * 出牌
     * @param step
     * @param posId
     * @param cards
     * @param remainingCardSize
     */
    public OperationCardResponse(Integer step, Integer posId, CardDTO cards, Integer remainingCardSize) {
        this.step = step;
        this.posId = posId;
        this.cards = cards;
        this.remainingCardSize = remainingCardSize;
    }

    public OperationCardResponse setStep(Integer step) {
        this.step = step;
        return this;
    }

    public OperationCardResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public OperationCardResponse setCards(CardDTO cards) {
        this.cards = cards;
        return this;
    }

    public OperationCardResponse setRemainingCardSize(Integer remainingCardSize) {
        this.remainingCardSize = remainingCardSize;
        return this;
    }

    public Integer getStep() {
        return step;
    }

    public Integer getPosId() {
        return posId;
    }

    public CardDTO getCards() {
        return cards;
    }

    public Integer getRemainingCardSize() {
        return remainingCardSize;
    }

    @Override
    public String toString() {
        return "OperationCardResponse{" +
                "step=" + step +
                ", posId=" + posId +
                ", cards=" + cards +
                ", remainingCardSize=" + remainingCardSize +
                '}';
    }
}
