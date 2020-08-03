package com.yude.game.doudizhu.application.service.impl;


import com.yude.game.common.manager.IRoomManager;
import com.yude.game.doudizhu.application.request.MatchRequest;
import com.yude.game.doudizhu.application.service.JoinRoomService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: HH
 * @Date: 2020/6/18 16:07
 * @Version: 1.0
 * @Declare:
 */
@Service
public class JoinRoomServiceImpl implements JoinRoomService {

    @Autowired
    IRoomManager roomManager;

    @Override
    public void match(MatchRequest request) {
        roomManager.match(request.getUserId());
    }
}
