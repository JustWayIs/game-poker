package com.yude.game.doudizhu.application.response;

import com.yude.game.doudizhu.application.response.dto.CardDTO;
import com.yude.protocol.common.response.BaseResponse;


import java.util.List;

/**
 * @Author: HH
 * @Date: 2020/7/27 15:55
 * @Version: 1.0
 * @Declare:
 */
public class OutCardTipsResponse extends BaseResponse {
    private List<CardDTO> tips;

    public OutCardTipsResponse() {
    }

    public OutCardTipsResponse(List<CardDTO> tips) {
        this.tips = tips;
    }

    public List<CardDTO> getTips() {
        return tips;
    }

    public OutCardTipsResponse setTips(List<CardDTO> tips) {
        this.tips = tips;
        return this;
    }

    @Override
    public String toString() {
        return "OutCardTipsResponse{" +
                "tips=" + tips +
                '}';
    }
}
