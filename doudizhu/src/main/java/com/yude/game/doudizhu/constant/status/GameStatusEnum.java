package com.yude.game.doudizhu.constant.status;

import com.yude.game.poker.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/7/8 11:13
 * @Version: 1.0
 * @Declare:
 */
public enum GameStatusEnum implements Status {
    /**
     * 发牌 : 这个如果都能超时，说明这场游戏应该解散了
     */
    DEAL_CARD(999),
    /**
     * 叫分
     */
    CALL_SCORE(8),
    /**
     * 农民加倍
     */
    FARMERS_REDOUBLE(8),
    /**
     * 地主加倍
     */
    LANDOWNERS_REDOUBLE(8),
    /**
     * 行牌阶段
     */
    OPERATION_CARD(15),


    /**
     * 结算阶段
     */
    SETTLEMENT(999);

    private int timeoutTime;

    GameStatusEnum(int timeoutTime) {
        this.timeoutTime = timeoutTime;
    }

    public int getTimeoutTime() {
        return timeoutTime;
    }

    @Override
    public int status() {
        return this.ordinal();
    }
}
