package com.pieceofcake.real_time_data.kisapi.dto.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetPeriodMarketPriceResponseVo;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetPeriodMarketPriceResponseDto {
    private String stckBsopDate;    // 주식 영업일자
    private Long stckOprc;  // 주식 시가
    private Long stckClpr;  // 주식 종가
    private Long stckHgpr;  // 주식 최고가
    private Long stckLwpr;  // 주식 최저가
    private Integer acmlVol;    // 누적 거래량
    private Long acmlTrPbmn; // 누적 거래 대금

    @Builder
    public GetPeriodMarketPriceResponseDto(String stckBsopDate, Long stckOprc, Long stckClpr, Long stckHgpr, Long stckLwpr, Integer acmlVol, Long acmlTrPbmn) {
        this.stckBsopDate = stckBsopDate;
        this.stckOprc = stckOprc;
        this.stckClpr = stckClpr;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.acmlVol = acmlVol;
        this.acmlTrPbmn = acmlTrPbmn;
    }


    public static List<GetPeriodMarketPriceResponseDto> toDto(String response) {
        List<GetPeriodMarketPriceResponseDto> result = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            JsonNode output2 = rootNode.path("output2");
            if (output2.isArray()) {
                for (JsonNode node : output2) {
                    String stckBsopDate = node.path("stck_bsop_date").asText();
                    Long stckClpr = node.path("stck_clpr").asLong();
                    Long stckOprc = node.path("stck_oprc").asLong();
                    Long stckHgpr = node.path("stck_hgpr").asLong();
                    Long stckLwpr = node.path("stck_lwpr").asLong();
                    Integer acmlVol = node.path("acml_vol").asInt();
                    Long acmlTrPbmn = node.path("acml_tr_pbmn").asLong();

                    result.add(GetPeriodMarketPriceResponseDto.builder()
                            .stckBsopDate(stckBsopDate)
                            .stckClpr(stckClpr)
                            .stckOprc(stckOprc)
                            .stckHgpr(stckHgpr)
                            .stckLwpr(stckLwpr)
                            .acmlVol(acmlVol)
                            .acmlTrPbmn(acmlTrPbmn)
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Intraday chart parsing failed: " + e.getMessage(), e);
        }
        return result;
    }

    public GetPeriodMarketPriceResponseVo toVo() {
        return GetPeriodMarketPriceResponseVo.builder()
                .stckBsopDate(stckBsopDate)
                .stckClpr(stckClpr)
                .stckOprc(stckOprc)
                .stckHgpr(stckHgpr)
                .stckLwpr(stckLwpr)
                .acmlVol(acmlVol)
                .acmlTrPbmn(acmlTrPbmn)
                .build();
    }
}
