package com.yude.game.doudizhu.application.service;

import com.yude.game.doudizhu.application.request.CallScoreRequest;
import com.yude.game.doudizhu.application.request.OperationCardRequest;
import com.yude.game.doudizhu.application.request.ReconnectionRequest;
import com.yude.game.doudizhu.application.request.RedoubleScoreRequest;
import com.yude.game.doudizhu.constant.status.GameStatusEnum;


/**
 * @Author: HH
 * @Date: 2020/7/7 17:32
 * @Version: 1.0
 * @Declare:
 */
public interface PlayerActionService {
    /**
     * 叫分
     * @param request
     */
    void callScore(CallScoreRequest request);

    /**
     * 加倍
     * @param request
     */
    void redouble(RedoubleScoreRequest request);

    /**
     * 出牌/压死/过
     * @param request
     */
    void operationCard(OperationCardRequest request);

    void reconnection(ReconnectionRequest request);

    default boolean validProcess(GameStatusEnum currentStatus, GameStatusEnum targetStatus){
        return currentStatus.equals(targetStatus);
    }
}
