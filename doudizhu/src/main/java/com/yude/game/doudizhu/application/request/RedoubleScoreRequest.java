package com.yude.game.doudizhu.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

/**
 * @Author: HH
 * @Date: 2020/7/7 18:03
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class RedoubleScoreRequest extends AbstractRequest {
    /**
     * 不用Boolean的原因：万一他搞个超级加倍呢。 值为RuleConfig 中的一个
     */
    private  int redoubleNum;

    private int posId;

    private Integer gameZoneId;

    private Long roomId;


    public int getRedoubleNum() {
        return redoubleNum;
    }

    public RedoubleScoreRequest setRedoubleNum(int redoubleNum) {
        this.redoubleNum = redoubleNum;
        return this;
    }

    public int getPosId() {
        return posId;
    }

    public RedoubleScoreRequest setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    public Integer getGameZoneId() {
        return gameZoneId;
    }

    public RedoubleScoreRequest setGameZoneId(Integer gameZoneId) {
        this.gameZoneId = gameZoneId;
        return this;
    }

    public Long getRoomId() {
        return roomId;
    }

    public RedoubleScoreRequest setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    @Override
    public String toString() {
        return "RedoubleScoreRequest{" +
                "redoubleNum=" + redoubleNum +
                ", posId=" + posId +
                ", gameZoneId=" + gameZoneId +
                ", roomId=" + roomId +
                '}';
    }
}
