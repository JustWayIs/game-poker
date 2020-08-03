package com.yude.game.doudizhu.timeout;

import com.yude.game.doudizhu.constant.command.CommandCode;
import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.DouDiZhuRoom;
import com.yude.game.doudizhu.domain.DouDiZhuZone;

import com.yude.game.doudizhu.application.request.CallScoreRequest;
import com.yude.game.poker.common.timeout.TimeoutRequestGenerator;

import java.util.List;
import java.util.Optional;

/**
 * @Author: HH
 * @Date: 2020/7/14 17:03
 * @Version: 1.0
 * @Declare:
 */
public class CallScoreRequestGenerator implements TimeoutRequestGenerator<CallScoreRequest, DouDiZhuRoom, GameStatusEnum> {

    private final GameStatusEnum callScoreStatus = GameStatusEnum.CALL_SCORE;


    @Override
    public CallScoreRequest build(DouDiZhuRoom cloneRoom) throws CloneNotSupportedException {
        //使用clone可能存在的问题： 因为是并发状态，所以在游戏内的属性可能处于一个 当前-变化 的临界区。
        //DouDiZhuRoom cloneRoom = (DouDiZhuRoom) cloneRoom.clone();
        List<Integer> callScoreOption = cloneRoom.getCallScoreOption();
        DouDiZhuZone douDiZhuZone = cloneRoom.getDouDiZhuZone();
        Long roomId = cloneRoom.getRoomId();
        CallScoreRequest request = new CallScoreRequest();
        //保证Option的第一个值是 不叫分
        request.setCallScore(callScoreOption.get(0));
        request.setPosId(douDiZhuZone.getCurrentOperatorPosId());
        request.setGameZoneId(douDiZhuZone.getZoneId());
        request.setRoomId(roomId);


        //这个方法会改变游戏的currentPosId属性
        List<Long> currentOperatorUserIds = cloneRoom.getCurrentOperatorUserIds();
        request.setUserIdByChannel(currentOperatorUserIds.get(0));
        return request;
    }

    @Override
    public Optional<CallScoreRequestGenerator> match(GameStatusEnum gameStatusEnum) {
        if(callScoreStatus.equals(gameStatusEnum)){
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public int getCmd() {
        return CommandCode.CALL_SOCRE;
    }
}
