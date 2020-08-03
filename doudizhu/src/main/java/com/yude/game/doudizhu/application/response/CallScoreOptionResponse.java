package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/10 16:00
 * @Version: 1.0
 * @Declare:
 */
public class CallScoreOptionResponse extends BaseResponse {
    private List<Integer> callScoreOption;

    public CallScoreOptionResponse() {
    }

    public List<Integer> getCallScoreOption() {
        return callScoreOption;
    }

    public CallScoreOptionResponse setCallScoreOption(List<Integer> callScoreOption) {
        this.callScoreOption = callScoreOption;
        return this;
    }

    public CallScoreOptionResponse(List<Integer> callScoreOption) {
        this.callScoreOption = callScoreOption;
    }

    @Override
    public String toString() {
        return "CallScoreOptionResponse{" +
                "callScoreOption=" + callScoreOption +
                '}';
    }
}
