package com.yude.game.doudizhu.application.request;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.protocol.common.request.AbstractRequest;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/6/10 20:24
 * @Version 1.0
 * @Declare
 */
@ProtobufClass
@EnableZigZap
public class OperationCardRequest extends AbstractRequest {
    private Long roomId;
    private Integer zoneId;
    private List<Integer> cards;
    private int posId;

    public Long getRoomId() {
        return roomId;
    }

    public OperationCardRequest setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public OperationCardRequest setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public OperationCardRequest setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public int getPosId() {
        return posId;
    }

    public OperationCardRequest setPosId(int posId) {
        this.posId = posId;
        return this;
    }

    @Override
    public String toString() {
        return "OperationCardRequest{" +
                "roomId=" + roomId +
                ", zoneId=" + zoneId +
                ", cards=" + cards +
                ", posId=" + posId +
                '}';
    }
}
