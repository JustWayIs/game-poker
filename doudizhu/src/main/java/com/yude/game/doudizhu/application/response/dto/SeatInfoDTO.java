package com.yude.game.doudizhu.application.response.dto;

import com.baidu.bjf.remoting.protobuf.annotation.EnableZigZap;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/11 15:12
 * @Version: 1.0
 * @Declare:
 */
@ProtobufClass
@EnableZigZap
public class SeatInfoDTO {
    private Integer posId;
    private PlayerDTO playerDTO;
    private List<Integer> handCards;
    /**
     * 还没有叫分，服务器用null,这里用-1
     */
    private Integer callScore;
    /**
     * 还没有进行 加倍操作，服务器用null，这里用-1
     */
    private Integer redouble;

    /**
     * 出牌提示
     */
    private List<CardDTO> outCardTips;

    public SeatInfoDTO() {
    }

    /**
     * 匹配完成时
     * @param posId
     * @param playerDTO
     */
    public SeatInfoDTO(Integer posId, PlayerDTO playerDTO) {
        this.posId = posId;
        this.playerDTO = playerDTO;
    }

    /**
     * 重连
     * @param posId
     * @param playerDTO
     * @param handCards
     * @param callScore
     * @param redouble
     */
    public SeatInfoDTO(Integer posId, PlayerDTO playerDTO, List<Integer> handCards, Integer callScore, Integer redouble) {
        this.posId = posId;
        this.playerDTO = playerDTO;
        this.handCards = handCards;
        this.callScore = callScore;
        this.redouble = redouble;
    }

    public SeatInfoDTO setPosId(Integer posId) {
        this.posId = posId;
        return this;
    }

    public SeatInfoDTO setPlayerDTO(PlayerDTO playerDTO) {
        this.playerDTO = playerDTO;
        return this;
    }

    public SeatInfoDTO setHandCards(List<Integer> handCards) {
        this.handCards = handCards;
        return this;
    }

    public SeatInfoDTO setCallScore(Integer callScore) {
        this.callScore = callScore;
        return this;
    }

    public SeatInfoDTO setRedouble(Integer redouble) {
        this.redouble = redouble;
        return this;
    }

    public SeatInfoDTO setOutCardTips(List<CardDTO> outCardTips) {
        this.outCardTips = outCardTips;
        return this;
    }

    public Integer getPosId() {
        return posId;
    }

    public PlayerDTO getPlayerDTO() {
        return playerDTO;
    }

    public List<Integer> getHandCards() {
        return handCards;
    }

    public Integer getCallScore() {
        return callScore;
    }

    public Integer getRedouble() {
        return redouble;
    }

    public List<CardDTO> getOutCardTips() {
        return outCardTips;
    }

    @Override
    public String toString() {
        return "SeatInfoDTO{" +
                "posId=" + posId +
                ", playerDTO=" + playerDTO +
                ", handCards=" + handCards +
                ", callScore=" + callScore +
                ", redouble=" + redouble +
                ", outCardTips=" + outCardTips +
                '}';
    }
}
