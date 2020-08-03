package com.yude.game.doudizhu.constant.command;

/**
 * @Author: HH
 * @Date: 2020/6/29 17:15
 * @Version: 1.0
 * @Declare:
 */
public interface PushCommandCode {
    /**
     * 匹配完成
     */
    int MATH_FINISH = 0x2001;

    /**
     * 游戏开始：发牌
     */
    int GAME_START = 0x2002;

    /**
     * 叫分选项列表
     */
    int CALL_SCORE_OPTION = 0x2003;

    /**
     * 叫分结果
     */
    int CALL_SCORE = 0x2004;

    /**
     * 地主归属
     */
    int LANDLORD_OWNERSHIP = 0x2005;

    /**
     * 加倍选项
     */
    int REDOUBLE_OPTION = 0x2006;

    /**
     * 农民加倍
     */
    int FARMERS_REDOUBLE = 0x2007;


    /**
     * 地主加倍
     */
    int LANDOWNERS_REDOUBLE = 0x2008;

    /**
     * 行牌
     */
    int OPEATION_CARD = 0x2009;

    /**
     * 结算
     */
    int SETTLEMENT = 0x2010;

    /**
     * 自己重连
     */
    int SELF_RECONNECT = 0x2011;

    /**
     * 别人重连
     */
    int OTHER_RECONNECT = 0x2012;

    /**
     * 游戏过程界面里的倍数详情
     */
    int REDOUBLE_DETAIL = 0x2013;

    /**
     * 春天
     */
    int SPRING_TIME = 0x2014;

    /**
     * 剩余牌警报
     */
    int CARD_REMAINING_ALARM = 0x2015;

    /**
     * 出牌提示
     */
    int OUT_CARD_TIPS = 0x2016;

    /**
     * 当前操作玩家
     */
    int CURRENT_OPERATOR = 0x200F;
}
