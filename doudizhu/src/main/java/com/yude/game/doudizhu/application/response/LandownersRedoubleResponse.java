package com.yude.game.doudizhu.application.response;

import com.yude.game.doudizhu.application.response.dto.RedoubleDTO;
import com.yude.protocol.common.response.BaseResponse;


/**
 * @Author: HH
 * @Date: 2020/7/8 21:52
 * @Version: 1.0
 * @Declare:
 */
public class LandownersRedoubleResponse extends BaseResponse {
    private RedoubleDTO redoubleDTO;
    //进入 行牌 阶段
    private Integer gameStatus;

    public LandownersRedoubleResponse() {
    }

    public LandownersRedoubleResponse(RedoubleDTO redoubleDTO, Integer gameStatus) {
        this.redoubleDTO = redoubleDTO;
        this.gameStatus = gameStatus;
    }

    public RedoubleDTO getRedoubleDTO() {
        return redoubleDTO;
    }

    public void setRedoubleDTO(RedoubleDTO redoubleDTO) {
        this.redoubleDTO = redoubleDTO;
    }

    public Integer getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Integer gameStatus) {
        this.gameStatus = gameStatus;
    }

    @Override
    public String toString() {
        return "LandownersRedoubleResponse{" +
                "redoubleDTO=" + redoubleDTO +
                ", gameStatus=" + gameStatus +
                '}';
    }
}
