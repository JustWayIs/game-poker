package com.yude.game.doudizhu.domain.history;


import com.yude.game.poker.common.model.Player;

/**
 * @Author: HH
 * @Date: 2020/6/28 20:25
 * @Version: 1.0
 * @Declare:  player信息其实也可以根据posId 和 roomId 查出来
 */
public class GameStepModel<T extends Step> implements GameHistory {
    private Integer zoneId;
    private Player players;
    private T operationStep;

    public GameStepModel(Integer zoneId, Player players, T operationStep) {
        this.zoneId = zoneId;
        this.players = players;
        this.operationStep = operationStep;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public Player getPlayers() {
        return players;
    }

    public T getOperationStep() {
        return operationStep;
    }

    @Override
    public String toString() {
        return "GameStepModel{" +
                "zoneId=" + zoneId +
                ", players=" + players +
                ", operationStep=" + operationStep +
                '}';
    }
}
