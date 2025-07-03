package com.pieceofcake.real_time_data.kisapi.vo.out;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetIntradayChartListResponseVo {
    private List<GetIntradayChartResponseVo> intradayChartList;

    @Builder
    public GetIntradayChartListResponseVo(List<GetIntradayChartResponseVo> intradayChartList) {
        this.intradayChartList = intradayChartList;
    }
}
