package com.yude.game.doudizhu.application.response.dto;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/10 16:34
 * @Version: 1.0
 * @Declare:
 */
public class SettlementDTO extends BaseResponse {
    private Integer step;
    private Integer posId;
    private Integer baseScore;
    private Integer resultRedoubleNum;
    private Integer changeScore;
    private List<Integer> handCards;

    public SettlementDTO() {
    }


    public Integer getStep() {
        return step;
    }

    public SettlementDTO setStep(Integer step) {
        this.step = step;
        return this;
    }

    public Integer getPosId() {
        return posId;
    }

    public SettlementDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public Integer getBaseScore() {
        return baseScore;
    }

    public SettlementDTO setBaseScore(Integer baseScore) {
        this.baseScore = baseScore;
        return this;
    }

    public Integer getResultRedoubleNum() {
        return resultRedoubleNum;
    }

    public SettlementDTO setResultRedoubleNum(Integer resultRedoubleNum) {
        this.resultRedoubleNum = resultRedoubleNum;
        return this;
    }

    public Integer getChangeScore() {
        return changeScore;
    }

    public SettlementDTO setChangeScore(Integer changeScore) {
        this.changeScore = changeScore;
        return this;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public SettlementDTO setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementDTO{" +
                "step=" + step +
                ", posId=" + posId +
                ", baseScore=" + baseScore +
                ", resultRedoubleNum=" + resultRedoubleNum +
                ", changeScore=" + changeScore +
                ", handCards=" + handCards +
                '}';
    }
}
