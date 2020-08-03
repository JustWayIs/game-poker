package com.yude.game.doudizhu.application.response;

import com.yude.game.doudizhu.application.response.dto.SeatInfoDTO;
import com.yude.protocol.common.response.BaseResponse;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/6/29 20:50
 * @Version: 1.0
 * @Declare:
 */
public class MatchFinishResponse extends BaseResponse {
    private Long roomId;
    private List<SeatInfoDTO> seatInfoDTOList;

    public MatchFinishResponse() {
    }

    public MatchFinishResponse(Long roomId, List<SeatInfoDTO> seatInfoDTOList) {
        this.roomId = roomId;
        this.seatInfoDTOList = seatInfoDTOList;
    }

    public Long getRoomId() {
        return roomId;
    }

    public MatchFinishResponse setRoomId(Long roomId) {
        this.roomId = roomId;
        return this;
    }

    public List<SeatInfoDTO> getSeatInfoDTOList() {
        return seatInfoDTOList;
    }

    public MatchFinishResponse setSeatInfoDTOList(List<SeatInfoDTO> seatInfoDTOList) {
        this.seatInfoDTOList = seatInfoDTOList;
        return this;
    }


    @Override
    public String toString() {
        return "MatchFinishResponse{" +
                "roomId=" + roomId +
                ", seatInfoDTOList=" + seatInfoDTOList +
                '}';
    }
}
