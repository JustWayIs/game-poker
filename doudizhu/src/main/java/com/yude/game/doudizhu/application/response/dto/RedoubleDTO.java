package com.yude.game.doudizhu.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;

/**
 * @Author: HH
 * @Date: 2020/7/8 21:55
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class RedoubleDTO {
    private Integer step;
    private Integer posId;
    private Integer redoubleNum;

    public RedoubleDTO() {
    }

    public RedoubleDTO(Integer step,Integer posId, Integer redoubleNum) {
        this.posId = posId;
        this.redoubleNum = redoubleNum;
    }

    public Integer getPosId() {
        return posId;
    }

    public void setPosId(Integer posId) {
        this.posId = posId;
    }

    public Integer getRedoubleNum() {
        return redoubleNum;
    }

    public void setRedoubleNum(Integer redoubleNum) {
        this.redoubleNum = redoubleNum;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    @Override
    public String toString() {
        return "RedoubleDTO{" +
                "step=" + step +
                ", posId=" + posId +
                ", redoubleNum=" + redoubleNum +
                '}';
    }
}
