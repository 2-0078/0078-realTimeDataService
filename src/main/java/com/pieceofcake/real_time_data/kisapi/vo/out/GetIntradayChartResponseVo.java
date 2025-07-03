package com.pieceofcake.real_time_data.kisapi.vo.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetIntradayChartResponseVo {
    private String stckCntgHour;    // 주식 체결시간
    private Long stckPrpr;  // 주식 현재가
    private Long stckOprc;  // 주식 시가
    private Long stckHgpr;  // 주식 최고가
    private Long stckLwpr;  // 주식 최저가
    private Integer cntgVol;    // 체결 거래량

    @Builder
    public GetIntradayChartResponseVo(String stckCntgHour, Long stckPrpr, Long stckOprc, Long stckHgpr, Long stckLwpr, Integer cntgVol) {
        this.stckCntgHour = stckCntgHour;
        this.stckPrpr = stckPrpr;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.cntgVol = cntgVol;
    }
}
