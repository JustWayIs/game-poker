package com.yude.game.doudizhu.timeout;

import com.yude.game.doudizhu.constant.command.CommandCode;
import com.yude.game.doudizhu.constant.status.GameStatusEnum;
import com.yude.game.doudizhu.domain.DouDiZhuRoom;
import com.yude.game.doudizhu.domain.DouDiZhuZone;

import com.yude.game.doudizhu.application.request.OperationCardRequest;
import com.yude.game.poker.common.timeout.TimeoutRequestGenerator;


import java.util.List;
import java.util.Optional;

/**
 * @Author: HH
 * @Date: 2020/7/14 17:14
 * @Version: 1.0
 * @Declare:
 */
public class OperationCardRequestGenerator implements TimeoutRequestGenerator<OperationCardRequest, DouDiZhuRoom, GameStatusEnum> {

    private final GameStatusEnum operationCardStatus = GameStatusEnum.OPERATION_CARD;


    @Override
    public OperationCardRequest build(DouDiZhuRoom cloneRoom) throws CloneNotSupportedException {
        //DouDiZhuRoom cloneRoom = (DouDiZhuRoom) douDiZhuRoom.clone();

        Long roomId = cloneRoom.getRoomId();
        DouDiZhuZone douDiZhuZone = cloneRoom.getDouDiZhuZone();
        List<Integer> timeoutOutCard = cloneRoom.getTimeoutOutCard();
        if(timeoutOutCard == null){
            return null;
        }

        OperationCardRequest request = new OperationCardRequest();
        request.setRoomId(roomId)
                .setZoneId(douDiZhuZone.getZoneId())
                .setPosId(douDiZhuZone.getCurrentOperatorPosId())
                .setCards(timeoutOutCard);

        List<Long> currentOperatorUserIds = cloneRoom.getCurrentOperatorUserIds();
        request.setUserIdByChannel(currentOperatorUserIds.get(0));

        return request;
    }

    @Override
    public Optional<OperationCardRequestGenerator> match(GameStatusEnum gameStatusEnum) {
        if(operationCardStatus.equals(gameStatusEnum)){
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public int getCmd() {
        return CommandCode.OPERATION_CARD;
    }
}
