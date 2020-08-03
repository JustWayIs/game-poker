package com.yude.game.doudizhu.constant.status;

import com.yude.game.poker.common.constant.Status;

/**
 * @Author: HH
 * @Date: 2020/7/8 11:41
 * @Version: 1.0
 * @Declare:
 */
public enum SeatStatusEnum implements Status {
    /**
     * 处于发牌阶段
     */
    DEAL_CARD,
    /**
     * 处于 叫分阶段，意味着玩家还没有叫分
     */
    CALL_SCORE,

    /**
     * 处于 加倍阶段，意味着玩家还没有加倍
     * <p>
     * 逗号便于从后面继续添加
     */
    REDOUBLE,

    /**
     * 行牌阶段
     */
    OPERATION_CARD,
    ;


    @Override
    public int status() {
        return this.ordinal();
    }
}
