package com.yude.game.doudizhu.constant.command;

import com.yude.game.poker.common.command.BaseCommandCode;

/**
 * @Author: HH
 * @Date: 2020/6/17 20:40
 * @Version: 1.0
 * @Declare:
 */
public interface CommandCode extends BaseCommandCode {

    /**
     * 登录
     */
    int LOGIN = 0x1001;

    /**
     * 匹配
     */
    int MATCH = 0x1003;

    /**
     * 叫分
     */
    int CALL_SOCRE = 0x1004;

    /**
     * 加倍
     */
    int REDOUBLE_SCORE = 0x1005;

    /**
     * 出牌/压死/过
     */
    int OPERATION_CARD = 0x1006;

    /**
     * 断线重连
     */
    int RECONNECTION = 0x1007;

    /**
     * 用不上
     */
    /**
     * 创建房间
     */
    //int CREATE_ROOM = 0x1002;

    /**
     * 快速开始
     */
    //int QUICK_START = 0x1003;

    /**
     * 准备
     */
    //int READY = 0x1004;

}
