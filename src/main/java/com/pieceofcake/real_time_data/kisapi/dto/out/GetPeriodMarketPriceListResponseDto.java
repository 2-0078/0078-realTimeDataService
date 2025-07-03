package com.pieceofcake.real_time_data.kisapi.dto.out;

import com.pieceofcake.real_time_data.kisapi.vo.out.GetPeriodMarketPriceListResponseVo;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class GetPeriodMarketPriceListResponseDto {
    private List<GetPeriodMarketPriceResponseDto> periodMarketResponseDtoList;

    @Builder
    public GetPeriodMarketPriceListResponseDto(List<GetPeriodMarketPriceResponseDto> periodMarketResponseDtoList) {
        this.periodMarketResponseDtoList = periodMarketResponseDtoList;
    }

    public static GetPeriodMarketPriceListResponseDto toDto(String response) {
        return GetPeriodMarketPriceListResponseDto.builder()
                .periodMarketResponseDtoList(GetPeriodMarketPriceResponseDto.toDto(response))
                .build();
    }

    public GetPeriodMarketPriceListResponseVo toVo() {
        return GetPeriodMarketPriceListResponseVo.builder()
                .periodMarketPriceList(periodMarketResponseDtoList.stream()
                        .map(GetPeriodMarketPriceResponseDto::toVo).toList())
                .build();
    }
}
