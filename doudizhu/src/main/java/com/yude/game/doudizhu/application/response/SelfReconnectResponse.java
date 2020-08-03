package com.yude.game.doudizhu.application.response;

import com.yude.game.doudizhu.application.response.dto.GameZoneInfoDTO;
import com.yude.game.doudizhu.application.response.dto.SeatInfoDTO;
import com.yude.protocol.common.response.BaseResponse;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/10 20:56
 * @Version: 1.0
 * @Declare:
 */
public class SelfReconnectResponse extends BaseResponse {
    private GameZoneInfoDTO gameZoneInfoDTO;
    private List<SeatInfoDTO> seatInfoDTOList;

    public SelfReconnectResponse() {
    }

    public SelfReconnectResponse(GameZoneInfoDTO gameZoneInfoDTO, List<SeatInfoDTO> seatInfoDTOList) {
        this.gameZoneInfoDTO = gameZoneInfoDTO;
        this.seatInfoDTOList = seatInfoDTOList;
    }

    public GameZoneInfoDTO getGameZoneInfoDTO() {
        return gameZoneInfoDTO;
    }

    public SelfReconnectResponse setGameZoneInfoDTO(GameZoneInfoDTO gameZoneInfoDTO) {
        this.gameZoneInfoDTO = gameZoneInfoDTO;
        return this;
    }

    public List<SeatInfoDTO> getSeatInfoDTOList() {
        return seatInfoDTOList;
    }

    public SelfReconnectResponse setSeatInfoDTOList(List<SeatInfoDTO> seatInfoDTOList) {
        this.seatInfoDTOList = seatInfoDTOList;
        return this;
    }

    @Override
    public String toString() {
        return "SelfReconnectResponse{" +
                "gameZoneInfoDTO=" + gameZoneInfoDTO +
                ", seatInfoDTOList=" + seatInfoDTOList +
                '}';
    }
}
