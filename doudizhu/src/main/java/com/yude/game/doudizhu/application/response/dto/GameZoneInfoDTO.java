package com.yude.game.doudizhu.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import com.yude.game.doudizhu.application.response.RedoubleDetailResponse;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/11 15:15
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class GameZoneInfoDTO {
    private Long roomId;
    private Integer zoneId;
    private Integer gameStatus;
    private List<Integer> currentOperatorPosId;

    private CardDTO lastOutCard;
    private Integer lastOutCardPosId;
    private Integer lastOperationPosId;

    private Integer landlordPosId;
    /**
     * 地主分，不是地主叫的分。重开局，强制选地主的时候，没有设置地主叫分值
     */
    private Integer landlordScore;
    private List<Integer> holeCards;

    private List<Integer> callScoreOptions;
    private List<Integer> redoubleNumOptions;

    private Long remaningTime;

    private RedoubleDetailResponse redoubleDetail;

    public GameZoneInfoDTO() {
    }

    public GameZoneInfoDTO setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public GameZoneInfoDTO setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    public GameZoneInfoDTO setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public GameZoneInfoDTO setCurrentOperatorPosId(List<Integer> currentOperatorPosId) {
        this.currentOperatorPosId = currentOperatorPosId;
        return this;
    }

    public GameZoneInfoDTO setLastOutCard(CardDTO lastOutCard) {
        this.lastOutCard = lastOutCard;
        return this;
    }

    public GameZoneInfoDTO setLastOutCardPosId(Integer lastOutCardPosId) {
        this.lastOutCardPosId = lastOutCardPosId;
        return this;
    }

    public GameZoneInfoDTO setLastOperationPosId(Integer lastOperationPosId) {
        this.lastOperationPosId = lastOperationPosId;
        return this;
    }

    public GameZoneInfoDTO setLandlordPosId(Integer landlordPosId) {
        this.landlordPosId = landlordPosId;
        return this;
    }

    public GameZoneInfoDTO setLandlordScore(Integer landlordScore) {
        this.landlordScore = landlordScore;
        return this;
    }

    public GameZoneInfoDTO setHoleCards(List<Integer> holeCards) {
        this.holeCards = holeCards;
        return this;
    }

    public GameZoneInfoDTO setCallScoreOptions(List<Integer> callScoreOptions) {
        this.callScoreOptions = callScoreOptions;
        return this;
    }

    public GameZoneInfoDTO setRedoubleNumOptions(List<Integer> redoubleNumOptions) {
        this.redoubleNumOptions = redoubleNumOptions;
        return this;
    }

    public GameZoneInfoDTO setRedoubleDetail(RedoubleDetailResponse redoubleDetail) {
        this.redoubleDetail = redoubleDetail;
        return this;
    }

    public Long getRoomId() {
        return roomId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public Integer getGameStatus() {
        return gameStatus;
    }

    public List<Integer> getCurrentOperatorPosId() {
        return currentOperatorPosId;
    }

    public CardDTO getLastOutCard() {
        return lastOutCard;
    }

    public Integer getLastOutCardPosId() {
        return lastOutCardPosId;
    }

    public Integer getLastOperationPosId() {
        return lastOperationPosId;
    }

    public Integer getLandlordPosId() {
        return landlordPosId;
    }

    public Integer getLandlordScore() {
        return landlordScore;
    }

    public List<Integer> getHoleCards() {
        return holeCards;
    }

    public List<Integer> getCallScoreOptions() {
        return callScoreOptions;
    }

    public Long getRemaningTime() {
        return remaningTime;
    }

    public GameZoneInfoDTO setRemaningTime(Long remaningTime) {
        this.remaningTime = remaningTime;
        return this;
    }

    public List<Integer> getRedoubleNumOptions() {
        return redoubleNumOptions;
    }

    public RedoubleDetailResponse getRedoubleDetail() {
        return redoubleDetail;
    }

    @Override
    public String toString() {
        return "GameZoneInfoDTO{" +
                "roomId=" + roomId +
                ", zoneId=" + zoneId +
                ", gameStatus=" + gameStatus +
                ", currentOperatorPosId=" + currentOperatorPosId +
                ", lastOutCard=" + lastOutCard +
                ", lastOutCardPosId=" + lastOutCardPosId +
                ", lastOperationPosId=" + lastOperationPosId +
                ", landlordPosId=" + landlordPosId +
                ", landlordScore=" + landlordScore +
                ", holeCards=" + holeCards +
                ", callScoreOptions=" + callScoreOptions +
                ", redoubleNumOptions=" + redoubleNumOptions +
                ", remaningTime=" + remaningTime +
                ", redoubleDetail=" + redoubleDetail +
                '}';
    }
}
