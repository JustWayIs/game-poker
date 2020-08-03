package com.yude.game.doudizhu.application.controller;

/**
 * @Author: HH
 * @Date: 2020/6/22 17:57
 * @Version: 1.0
 * @Declare:
 */

import com.yude.game.common.command.annotation.RequestCommand;
import com.yude.game.common.command.annotation.RequestController;
import com.yude.game.common.exception.BizException;
import com.yude.game.doudizhu.constant.command.CommandCode;
import com.yude.game.doudizhu.application.request.CallScoreRequest;
import com.yude.game.doudizhu.application.request.OperationCardRequest;
import com.yude.game.doudizhu.application.request.ReconnectionRequest;
import com.yude.game.doudizhu.application.request.RedoubleScoreRequest;
import com.yude.game.doudizhu.application.service.PlayerActionService;


import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.response.CommonResponse;
import com.yude.protocol.common.response.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@RequestController
public class ZoneActionController  implements BaseController{
    private static final Logger log = LoggerFactory.getLogger(ZoneActionController.class);

    @Autowired
    private PlayerActionService playerActionService;

    @RequestCommand(CommandCode.CALL_SOCRE)
    public void callScore(CallScoreRequest request){
        log.info("收到叫分请求： request={}",request);
        Integer callScore = request.getCallScore();
        if(callScore == null){
            throw new BizException(StatusCodeEnum.PARAM_VALID_FAIL);
        }
        playerActionService.callScore(request);
    }

    @RequestCommand(CommandCode.REDOUBLE_SCORE)
    public Response doubleScore(RedoubleScoreRequest request){
        log.info("收到加倍操作请求： request={}",request);
        playerActionService.redouble(request);
        CommonResponse response = new CommonResponse(StatusCodeEnum.SUCCESS);
        return response;
    }

    /**
     * 暂时不支持多参数方法
     * @param request
     * @return
     */
    @RequestCommand(CommandCode.OPERATION_CARD)
    public void operationCard(OperationCardRequest request){
        log.info("收到行牌请求： request={}",request);
        playerActionService.operationCard(request);
    }

    @RequestCommand(CommandCode.RECONNECTION)
    public void reconnection(ReconnectionRequest request){
        if(!validUser(request,request.getUserId())){
            throw new BizException("匹配用户校验失败,channel保存的玩家标识 与请求参数中的玩家标识不一致",StatusCodeEnum.MATCH_VALID_FAIL);
        }
        playerActionService.reconnection(request);
    }
}
