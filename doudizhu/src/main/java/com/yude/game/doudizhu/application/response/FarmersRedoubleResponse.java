package com.yude.game.doudizhu.application.response;

import com.yude.game.doudizhu.application.response.dto.RedoubleDTO;
import com.yude.protocol.common.response.BaseResponse;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/7 18:10
 * @Version: 1.0
 * @Declare:
 */
public class FarmersRedoubleResponse extends BaseResponse {
    private List<RedoubleDTO> list;
    private Integer gameStatus;

    public FarmersRedoubleResponse() {
    }

    public FarmersRedoubleResponse(List<RedoubleDTO> list, Integer gameStatus) {
        this.list = list;
        this.gameStatus = gameStatus;
    }

    public List<RedoubleDTO> getList() {
        return list;
    }

    public void setList(List<RedoubleDTO> list) {
        this.list = list;
    }

    public Integer getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public String toString() {
        return "FarmersRedoubleResponse{" +
                "list=" + list +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
