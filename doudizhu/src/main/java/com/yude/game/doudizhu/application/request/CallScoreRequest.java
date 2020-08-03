package com.yude.game.doudizhu.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

/**
 * @Author: HH
 * @Date: 2020/7/7 15:58
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class CallScoreRequest extends AbstractRequest {
    /**
     * 叫分: 客户端序列化机制问题，0传不过来，所以用int类型来弥补，当然实际上callScore本来也不该有null,只不过用int就分不清 值为0 到底是客户端没有传值，还是就是传的0
     */
    private int callScore;



    /**
     * 需要在后面校验是不是一致:作为重复进入不同房间、或者同一个房间 不同位置的提示  后面可以去掉。没有实际意义
     *
     */
    private int posId;

    private Integer gameZoneId;

    private Long roomId;


    public int getCallScore() {
        return callScore;
    }

    public void setCallScore(int callScore) {
        this.callScore = callScore;
    }

    public int getPosId() {
        return posId;
    }

    public void setPosId(int posId) {
        this.posId = posId;
    }

    public Integer getGameZoneId() {
        return gameZoneId;
    }

    public void setGameZoneId(Integer gameZoneId) {
        this.gameZoneId = gameZoneId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "CallScoreRequest{" +
                "callScore=" + callScore +
                ", posId=" + posId +
                ", gameZoneId=" + gameZoneId +
                ", roomId=" + roomId +
                '}';
    }
}
