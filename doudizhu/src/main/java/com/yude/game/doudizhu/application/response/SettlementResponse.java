package com.yude.game.doudizhu.application.response;

import com.yude.game.doudizhu.application.response.dto.SettlementDTO;
import com.yude.protocol.common.response.BaseResponse;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/10 16:33
 * @Version: 1.0
 * @Declare:
 */
public class SettlementResponse extends BaseResponse {
    List<SettlementDTO> settlementDTOList;

    public SettlementResponse() {
    }

    public SettlementResponse(List<SettlementDTO> settlementDTOList) {
        this.settlementDTOList = settlementDTOList;
    }

    public List<SettlementDTO> getSettlementDTOList() {
        return settlementDTOList;
    }

    public SettlementResponse setSettlementDTOList(List<SettlementDTO> settlementDTOList) {
        this.settlementDTOList = settlementDTOList;
        return this;
    }

    @Override
    public String toString() {
        return "SettlementResponse{" +
                "settlementDTOList=" + settlementDTOList +
                '}';
    }
}
