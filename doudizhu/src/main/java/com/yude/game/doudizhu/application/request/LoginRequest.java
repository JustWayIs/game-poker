package com.yude.game.doudizhu.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

/**
 * @Author: HH
 * @Date: 2020/6/19 15:04
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class LoginRequest extends AbstractRequest {
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "userId=" + userId +
                '}';
    }
}
