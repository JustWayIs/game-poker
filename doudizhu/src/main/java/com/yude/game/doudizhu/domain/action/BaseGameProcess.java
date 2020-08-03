package com.yude.game.doudizhu.domain.action;

import com.yude.game.doudizhu.domain.card.Card;

/**
 * @Author: HH
 * @Date: 2020/6/29 17:10
 * @Version: 1.0
 * @Declare: 游戏主体流程
 */
public interface BaseGameProcess {
    void start(Long roomId);

    /**
     * 叫分
     * @param posId
     * @param score
     * @return 返回成为地主的玩家，还没有就返回null
     */
    Integer callScore(int posId, int score);

    /**
     * 农民加倍
     * @return 是否已完成加倍流程
     */
    boolean farmersRedouble(int redoubleValue);

    /**
     * 地主加倍
     * @return
     */
    boolean landownersRedouble();

    /**
     *  行牌
     * @param posId
     * @param card 为0的时候，可以认为是执行过操作 / 或者用可变参数来表达更符合语义
     * @return 是否有炸弹 ： 所有这样使用返回值的地方都有隐患
     */
    boolean operationCard(int posId, Card card);

    void gameOver(int winnerPosId);

    /**
     * 结算
     */
    void settlement();
}
