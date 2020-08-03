package com.yude.game.doudizhu.timeout;

import com.yude.game.common.timeout.TimeoutRequestGenerator;
import com.yude.game.doudizhu.constant.command.CommandCode;
import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.DouDiZhuRoom;
import com.yude.game.doudizhu.domain.DouDiZhuZone;

import com.yude.game.doudizhu.application.request.RedoubleScoreRequest;

import java.util.List;
import java.util.Optional;


/**
 * @Author: HH
 * @Date: 2020/7/14 17:09
 * @Version: 1.0
 * @Declare:
 */
public class RedoubleScoreRequestGenerator implements TimeoutRequestGenerator<RedoubleScoreRequest, DouDiZhuRoom,GameStatusEnum> {

    private final GameStatusEnum farmerRedoubleStatus = GameStatusEnum.FARMERS_REDOUBLE;
    private final GameStatusEnum landlordRedoubleStatus = GameStatusEnum.LANDOWNERS_REDOUBLE;


    @Override
    public RedoubleScoreRequest build(DouDiZhuRoom cloneRoom) throws CloneNotSupportedException {
        //DouDiZhuRoom cloneRoom = (DouDiZhuRoom) douDiZhuRoom.clone();

        List<Long> currentOperatorUserIds = cloneRoom.getCurrentOperatorUserIds();
        Integer[] redoubleOption = cloneRoom.getRedoubleOption();
        RedoubleScoreRequest request = null;
        for (Long userId : currentOperatorUserIds) {
            DouDiZhuZone douDiZhuZone = cloneRoom.getDouDiZhuZone();
            Long roomId = cloneRoom.getRoomId();
            request = new RedoubleScoreRequest();
            //保证第一个值是不加倍
            request.setRedoubleNum(redoubleOption[0]);
            //实际上使用的是服务器自己取的 根据channelUserId(加倍的流程中，operatorPosId是不准确的)，所以了影响不大，也可以从协议中去掉这个玩意儿
            request.setPosId(douDiZhuZone.getCurrentOperatorPosId());
            request.setGameZoneId(douDiZhuZone.getZoneId());
            request.setRoomId(roomId);

            request.setUserIdByChannel(userId);
        }
        return request;
    }

    @Override
    public Optional<RedoubleScoreRequestGenerator> match(GameStatusEnum gameStatusEnum) {
        if(farmerRedoubleStatus.equals(gameStatusEnum) || landlordRedoubleStatus.equals(gameStatusEnum)){
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public int getCmd() {
        return CommandCode.REDOUBLE_SCORE;
    }
}
