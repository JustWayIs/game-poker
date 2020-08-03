package com.yude.game.doudizhu.application.response;

import com.yude.protocol.common.response.BaseResponse;

/**
 * @Author: HH
 * @Date: 2020/7/13 15:07
 * @Version: 1.0
 * @Declare:
 */
public class RedoubleDetailResponse extends BaseResponse {
    /**
     * 底分：房间底注、初始分
     */
    private Integer baseScore;

    /**
     * 炸弹数量
     */
    private Integer bombCount;
    /**
     * 春天： 0/1
     */
    private Integer springTimeCount;
    /**
     * 公共倍数 = 炸弹数 + 春天
     */
    private Integer commonRedoubleNum;
    private Integer landlordRedoubleNum;
    /**
     * 地主视角：农民阵营的倍数。  农民视角：该农民自己的倍数
     */
    private Integer farmerRedoubleNum;

    /**
     * 总倍数
     */
    private Integer resultRedoubleNum;

    public Integer getBombCount() {
        return bombCount;
    }

    public Integer getSpringTimeCount() {
        return springTimeCount;
    }

    public Integer getCommonRedoubleNum() {
        return commonRedoubleNum;
    }

    public Integer getLandlordRedoubleNum() {
        return landlordRedoubleNum;
    }

    public Integer getFarmerRedoubleNum() {
        return farmerRedoubleNum;
    }

    public RedoubleDetailResponse setBaseScore(Integer baseScore) {
        this.baseScore = baseScore;
        return this;
    }


    public RedoubleDetailResponse setBombCount(Integer bombCount) {
        this.bombCount = bombCount;
        return this;
    }

    public RedoubleDetailResponse setSpringTimeCount(Integer springTimeCount) {
        this.springTimeCount = springTimeCount;
        return this;
    }

    public RedoubleDetailResponse setCommonRedoubleNum(Integer commonRedoubleNum) {
        this.commonRedoubleNum = commonRedoubleNum;
        return this;
    }

    public RedoubleDetailResponse setLandlordRedoubleNum(Integer landlordRedoubleNum) {
        this.landlordRedoubleNum = landlordRedoubleNum;
        return this;
    }

    public RedoubleDetailResponse setFarmerRedoubleNum(Integer farmerRedoubleNum) {
        this.farmerRedoubleNum = farmerRedoubleNum;
        return this;
    }

    public RedoubleDetailResponse setResultRedoubleNum(Integer resultRedoubleNum) {
        this.resultRedoubleNum = resultRedoubleNum;
        return this;
    }

    public Integer getBaseScore() {
        return baseScore;
    }

    public Integer getResultRedoubleNum() {
        return resultRedoubleNum;
    }

    @Override
    public String toString() {
        return "RedoubleDetailResponse{" +
                "baseScore=" + baseScore +
                ", bombCount=" + bombCount +
                ", springTimeCount=" + springTimeCount +
                ", commonRedoubleNum=" + commonRedoubleNum +
                ", landlordRedoubleNum=" + landlordRedoubleNum +
                ", farmerRedoubleNum=" + farmerRedoubleNum +
                ", resultRedoubleNum=" + resultRedoubleNum +
                '}';
    }
}
