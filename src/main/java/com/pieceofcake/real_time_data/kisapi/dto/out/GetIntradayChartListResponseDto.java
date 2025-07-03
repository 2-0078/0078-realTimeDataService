package com.pieceofcake.real_time_data.kisapi.dto.out;

import com.pieceofcake.real_time_data.kisapi.vo.out.GetIntradayChartListResponseVo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetIntradayChartListResponseDto {
    private List<GetIntradayChartResponseDto> intradayChartResponseDtoList;

    @Builder
    public GetIntradayChartListResponseDto(List<GetIntradayChartResponseDto> intradayChartResponseDtoList) {
        this.intradayChartResponseDtoList = intradayChartResponseDtoList;
    }

    public static GetIntradayChartListResponseDto toDto(String response) {
        return GetIntradayChartListResponseDto.builder()
                .intradayChartResponseDtoList(GetIntradayChartResponseDto.toDto(response))
                .build();
    }

    public GetIntradayChartListResponseVo toVo() {
        return GetIntradayChartListResponseVo.builder()
                .intradayChartList(intradayChartResponseDtoList.stream()
                        .map(GetIntradayChartResponseDto::toVo).toList())
                .build();
    }
}
