package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/9 15:56
 * @Version: 1.0
 * @Declare:
 */
public class CurrentOperatorResponse extends BaseResponse {
    private List<Integer> operatorList;
    private int seconds;

    public CurrentOperatorResponse() {
    }

    public CurrentOperatorResponse(List<Integer> operatorList, int seconds) {
        this.operatorList = operatorList;
        this.seconds = seconds;
    }

    public List<Integer> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<Integer> operatorList) {
        this.operatorList = operatorList;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    @Override
    public String toString() {
        return "CurrentOperatorResponse{" +
                "operatorList=" + operatorList +
                ", seconds=" + seconds +
                '}';
    }
}
