package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/7/16 11:40
 * @Version: 1.0
 * @Declare:
 */
public class SpringTimeResponse extends BaseResponse {
    private Boolean springTime;
    private Integer who;

    public SpringTimeResponse() {
    }

    public SpringTimeResponse(Boolean springTime, Integer who) {
        this.springTime = springTime;
        this.who = who;
    }

    public Boolean getSpringTime() {
        return springTime;
    }

    public void setSpringTime(Boolean springTime) {
        this.springTime = springTime;
    }

    public Integer getWho() {
        return who;
    }

    public SpringTimeResponse setWho(Integer who) {
        this.who = who;
        return this;
    }

    @Override
    public String toString() {
        return "SpringTimeResponse{" +
                "springTime=" + springTime +
                ", who=" + who +
                '}';
    }
}
