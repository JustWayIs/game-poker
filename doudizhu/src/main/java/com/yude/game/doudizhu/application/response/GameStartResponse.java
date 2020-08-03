package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/7 11:41
 * @Version: 1.0
 * @Declare: 必须存在默认构造器
 */
public class GameStartResponse extends BaseResponse {
    private Integer step;
    private Long roomId;
    private Integer zoneId;
    private Integer posId;
    //叫分阶段
    private Integer gameStatus;

    /**
     *  从通信上来说，由于客户端的语言类型是不确定的，用cardList命名太具有特定语言标识，不符合其职能的标准化定义
     */
    private List<Integer> cards;

    public GameStartResponse() {
    }


    public Long getRoomId() {
        return roomId;
    }

    public GameStartResponse setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public GameStartResponse setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public Integer getStep() {
        return step;
    }

    public GameStartResponse setStep(Integer step) {
        this.step = step;
        return this;
    }

    public Integer getPosId() {
        return posId;
    }

    public GameStartResponse setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public List<Integer> getCards() {
        return cards;
    }

    public GameStartResponse setCards(List<Integer> cards) {
        this.cards = cards;
        return this;
    }

    public Integer getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public String toString() {
        return "GameStartResponse{" +
                "step=" + step +
                ", roomId=" + roomId +
                ", zoneId=" + zoneId +
                ", posId=" + posId +
                ", gameStatus=" + gameStatus +
                ", cards=" + cards +
                '}';
    }
}
