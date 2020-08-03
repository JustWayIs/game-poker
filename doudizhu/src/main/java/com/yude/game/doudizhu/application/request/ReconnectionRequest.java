package com.yude.game.doudizhu.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

/**
 * @Author: HH
 * @Date: 2020/7/11 14:17
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class ReconnectionRequest extends AbstractRequest {
    private Long userId;
    private Long roomId;

    public ReconnectionRequest() {
    }

    public ReconnectionRequest(Long userId, Long roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public Long getUserId() {
        return userId;
    }

    public ReconnectionRequest setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getRoomId() {
        return roomId;
    }

    public ReconnectionRequest setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    @Override
    public String toString() {
        return "ReconnectionRequest{" +
                "userId=" + userId +
                '}';
    }
}
