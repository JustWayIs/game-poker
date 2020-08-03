package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/7/17 17:54
 * @Version: 1.0
 * @Declare:
 */
public class CardRemainingAlarmResponse extends BaseResponse {
    private Integer posId;

    public CardRemainingAlarmResponse() {
    }

    public CardRemainingAlarmResponse(Integer posId) {
        this.posId = posId;
    }

    public Integer getPosId() {
        return posId;
    }

    public CardRemainingAlarmResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    @Override
    public String toString() {
        return "CardRemainingAlarmResponse{" +
                "posId=" + posId +
                '}';
    }
}
