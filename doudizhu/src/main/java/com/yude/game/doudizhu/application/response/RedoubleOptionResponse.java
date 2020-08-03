package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/9 10:26
 * @Version: 1.0
 * @Declare:
 */
public class RedoubleOptionResponse extends BaseResponse {
    private List<Integer> redoubleOption;

    public RedoubleOptionResponse() {
    }

    public RedoubleOptionResponse(List<Integer> redoubleOption) {
        this.redoubleOption = redoubleOption;
    }

    public List<Integer> getRedoubleOption() {
        return redoubleOption;
    }

    public void setRedoubleOption(List<Integer> redoubleOption) {
        this.redoubleOption = redoubleOption;
    }

    @Override
    public String toString() {
        return "RedoubleOptionResponse{" +
                "redoubleOption=" + redoubleOption +
                '}';
    }
}
