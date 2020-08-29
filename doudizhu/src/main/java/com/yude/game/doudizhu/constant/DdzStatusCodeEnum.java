package com.yude.game.doudizhu.constant;

import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.constant.StatusCodeI;

/**
 * @Author: HH
 * @Date: 2020/6/23 14:59
 * @Version: 1.0
 * @Declare:
 */
public enum DdzStatusCodeEnum implements StatusCodeI {


    /**
     * 成功
     */
    SUCCESS(StatusCodeEnum.SUCCESS.code(), "success"),

    /**
     * 失败
     */
    FAIL(StatusCodeEnum.FAIL.code(), "fail"),

    SERVICE_NOT_EXISTS(404, "不存在该服务"),

    MATCH_EXISTS(100, "已在匹配队列中"),

    ILLEGAL_CARD(101,"非法的牌值"),

    ILLEGAL_OUT(102,"该出牌不符合规则"),

    MATCH_VALID_FAIL(103,"玩家匹配校验失败"),

    ILLEGAL_CALL_SCORE(104,"不存在的叫分选项"),

    CALL_SCORE_ERRO(105,"叫分不能比前面玩家更小"),

    PARAM_VALID_FAIL(106,"参数校验失败"),

    PROCESS_ERROR_CALL_SCORE(107,"当前不是叫分流程"),

   PROCESS_ERROR_REDOUBLE(108,"当前不是加倍流程"),

    PROCESS_ERROR_OPERATION_CARD(109,"当前不是行牌阶段"),

    MUST_OUT_CARD(110,"当前玩家必须出牌"),

    MATCH_FAIL(111,"匹配失败"),;



    private int code;

    private String msg;

    DdzStatusCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String msg() {

        return msg;
    }


}
