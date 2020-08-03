package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/7/8 17:11
 * @Version: 1.0
 * @Declare:
 */
public class CallScoreResponse extends BaseResponse {
    private Integer step;
    private Integer posId;
    private Integer score;

    public CallScoreResponse() {
    }

    public CallScoreResponse(Integer step, Integer posId, Integer score) {
        this.step = step;
        this.posId = posId;
        this.score = score;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public Integer getPosId() {
        return posId;
    }

    public void setPosId(Integer posId) {
        this.posId = posId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "CallScoreResponse{" +
                "step=" + step +
                ", posId=" + posId +
                ", score=" + score +
                '}';
    }
}
