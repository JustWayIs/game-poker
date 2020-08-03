package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/7/11 13:11
 * @Version: 1.0
 * @Declare:
 */
public class OtherReconnectResponse extends BaseResponse {
    private Integer posId;

    public OtherReconnectResponse() {
    }

    public OtherReconnectResponse(Integer posId) {
        this.posId = posId;
    }

    public Integer getPosId() {
        return posId;
    }

    public OtherReconnectResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    @Override
    public String toString() {
        return "OtherReconnectResponse{" +
                "posId=" + posId +
                '}';
    }
}
