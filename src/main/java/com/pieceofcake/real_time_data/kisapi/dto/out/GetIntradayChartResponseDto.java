package com.pieceofcake.real_time_data.kisapi.dto.out;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pieceofcake.real_time_data.kisapi.vo.out.GetIntradayChartResponseVo;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetIntradayChartResponseDto {
    private String stckCntgHour;    // 주식 체결시간
    private Long stckPrpr;  // 주식 현재가
    private Long stckOprc;  // 주식 시가
    private Long stckHgpr;  // 주식 최고가
    private Long stckLwpr;  // 주식 최저가
    private Integer cntgVol;    // 체결 거래량

    @Builder
    public GetIntradayChartResponseDto(String stckCntgHour, Long stckPrpr, Long stckOprc, Long stckHgpr, Long stckLwpr, Integer cntgVol) {
        this.stckCntgHour = stckCntgHour;
        this.stckPrpr = stckPrpr;
        this.stckOprc = stckOprc;
        this.stckHgpr = stckHgpr;
        this.stckLwpr = stckLwpr;
        this.cntgVol = cntgVol;
    }

    public static List<GetIntradayChartResponseDto> toDto(String response) {
        List<GetIntradayChartResponseDto> result = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            JsonNode output2 = rootNode.path("output2");
            if (output2.isArray()) {
                for (JsonNode node : output2) {
                    String stckCntgHour = node.path("stck_cntg_hour").asText();
                    Long stckPrpr = node.path("stck_prpr").asLong();
                    Long stckOprc = node.path("stck_oprc").asLong();
                    Long stckHgpr = node.path("stck_hgpr").asLong();
                    Long stckLwpr = node.path("stck_lwpr").asLong();
                    Integer cntgVol = node.path("cntg_vol").asInt();

                    result.add(GetIntradayChartResponseDto.builder()
                            .stckCntgHour(stckCntgHour)
                            .stckPrpr(stckPrpr)
                            .stckOprc(stckOprc)
                            .stckHgpr(stckHgpr)
                            .stckLwpr(stckLwpr)
                            .cntgVol(cntgVol)
                            .build());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Intraday chart parsing failed: " + e.getMessage(), e);
        }
        return result;
    }

    public GetIntradayChartResponseVo toVo() {
        return GetIntradayChartResponseVo.builder()
                .stckCntgHour(stckCntgHour)
                .stckPrpr(stckPrpr)
                .stckOprc(stckOprc)
                .stckHgpr(stckHgpr)
                .stckLwpr(stckLwpr)
                .cntgVol(cntgVol)
                .build();
    }
}
