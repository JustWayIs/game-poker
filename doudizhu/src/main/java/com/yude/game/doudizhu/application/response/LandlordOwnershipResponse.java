package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/8 17:29
 * @Version: 1.0
 * @Declare:
 */
public class LandlordOwnershipResponse extends BaseResponse {

    private Integer landlordPosId;
    private List<Integer> holeCards;
    private Integer landlordScore;
    //进入 农民加倍 操作阶段
    private Integer gameStatus;

    public LandlordOwnershipResponse() {
    }

    public LandlordOwnershipResponse(Integer landlordPosId,Integer landlordScore,Integer gameStatus) {
        this.landlordPosId = landlordPosId;
        this.landlordScore = landlordScore;
        this.gameStatus = gameStatus;
    }


    public Integer getLandlordPosId() {
        return landlordPosId;
    }

    public void setLandlordPosId(Integer landlordPosId) {
        this.landlordPosId = landlordPosId;
    }

    public Integer getLandlordScore() {
        return landlordScore;
    }

    public LandlordOwnershipResponse setLandlordScore(Integer landlordScore) {
        this.landlordScore = landlordScore;
        return this;
    }

    public List<Integer> getHoleCards() {
        return holeCards;
    }

    public void setHoleCards(List<Integer> holeCards) {
        this.holeCards = holeCards;
    }

    public Integer getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public String toString() {
        return "LandlordOwnershipResponse{" +
                "landlordPosId=" + landlordPosId +
                ", holeCards=" + holeCards +
                ", landlordScore=" + landlordScore +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
