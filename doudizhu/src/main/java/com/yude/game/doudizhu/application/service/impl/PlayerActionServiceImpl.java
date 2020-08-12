package com.yude.game.doudizhu.application.service.impl;

import com.yude.game.common.exception.BizException;
import com.yude.game.common.manager.IRoomManager;
import com.yude.game.common.model.AbstractRoomModel;
import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.application.request.CallScoreRequest;
import com.yude.game.doudizhu.application.request.OperationCardRequest;
import com.yude.game.doudizhu.application.request.ReconnectionRequest;
import com.yude.game.doudizhu.application.request.RedoubleScoreRequest;
import com.yude.game.doudizhu.application.service.PlayerActionService;

import com.yude.game.doudizhu.domain.DouDiZhuRoom;
import com.yude.protocol.common.constant.StatusCodeEnum;
import com.yude.protocol.common.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: HH
 * @Date: 2020/7/7 19:56
 * @Version: 1.0
 * @Declare:
 */
@Service
public class PlayerActionServiceImpl implements PlayerActionService {
    private static final Logger log = LoggerFactory.getLogger(PlayerActionServiceImpl.class);

    @Autowired
    IRoomManager roomManager;

    DouDiZhuRoom getRoom(Request request){
        Long userId = request.getUserIdByChannel();
        //问题在于 强制类型转换 是否属于设计上的失误？ 如果添加一个抽象层的话看，像AbstractRoomModel 和 DouDiZhuRoom 的关系一样。把属性全部转换成protected 还是使用private的属性，用public的方法来获取？
        AbstractRoomModel room = roomManager.getRoomByUserId(userId);
        if(room == null){
            log.warn("玩家已经不在游戏中: userId={}",userId);
            //得判断是不是超时任务，如果是超时任务就不用抛这个异常
            // throw new BizException("玩家已不在游戏中");
        }
        return (DouDiZhuRoom) room;
    }

    @Override
    public void callScore(CallScoreRequest request) {
        DouDiZhuRoom roomModel = getRoom(request);
        int posId = roomModel.getPosId(request.getUserIdByChannel());
        GameStatusEnum gameStatus = roomModel.getGameStatus();
        if(!validProcess(gameStatus,GameStatusEnum.CALL_SCORE)){
            throw new BizException(StatusCodeEnum.PROCESS_ERROR_CALL_SCORE);
        }
        roomModel.callScore(posId,request.getCallScore());

    }

    @Override
    public void redouble(RedoubleScoreRequest request) {
        DouDiZhuRoom roomModel = getRoom(request);
        int posId = roomModel.getPosId(request.getUserIdByChannel());
        GameStatusEnum gameStatus = roomModel.getGameStatus();
        if(!validProcess(gameStatus,GameStatusEnum.FARMERS_REDOUBLE) && !validProcess(gameStatus,GameStatusEnum.LANDOWNERS_REDOUBLE)){
            throw new BizException(StatusCodeEnum.PROCESS_ERROR_REDOUBLE);
        }
        roomModel.redouble(posId,request.getRedoubleNum());

    }

    @Override
    public void operationCard(OperationCardRequest request) {
        DouDiZhuRoom roomModel = getRoom(request);
        int posId = roomModel.getPosId(request.getUserIdByChannel());
        GameStatusEnum gameStatus = roomModel.getGameStatus();
        if(!validProcess(gameStatus,GameStatusEnum.OPERATION_CARD)){
            throw new BizException(StatusCodeEnum.PROCESS_ERROR_OPERATION_CARD);
        }
        roomModel.operationCard(posId,request.getCards());
    }

    @Override
    public void reconnection(ReconnectionRequest request) {
        DouDiZhuRoom roomModel = getRoom(request);
        roomModel.reconnect(request.getUserId());
    }
}
