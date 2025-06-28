package com.pieceofcake.real_time_data.websocket.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GetRealTimeQuotesResponseDto {
    private String stockCode;
    private List<Long> askp;
    private List<Long> bidp;
    private List<Integer> askpRsqn;
    private List<Integer> bidRsqn;

    @Builder
    public GetRealTimeQuotesResponseDto(String stockCode, List<Long> askp, List<Long> bidp, List<Integer> askpRsqn, List<Integer> bidRsqn) {
        this.stockCode = stockCode;
        this.askp = askp;
        this.bidp = bidp;
        this.askpRsqn = askpRsqn;
        this.bidRsqn = bidRsqn;
    }

    public static GetRealTimeQuotesResponseDto toDto(String stockCode, String rawJson) {
        try {
            String[] parts = rawJson.split("\\|", 4);
            if (parts.length < 4) {
                return null;
            }

            String[] fields = parts[3].split("\\^");
            if (fields.length < 59) {
                return null;
            }

            List<Long> askp = new ArrayList<>();
            List<Long> bidp = new ArrayList<>();
            List<Integer> askpRsqn = new ArrayList<>();
            List<Integer> bidRsqn = new ArrayList<>();

            // 호가 (askp) 1~10
            for (int i = 3; i <= 12; i++) {
                askp.add(parseLong(fields[i]));
            }

            // 매수호가 (bidp) 1~10
            for (int i = 13; i <= 22; i++) {
                bidp.add(parseLong(fields[i]));
            }

            // 매도호가 잔량 (askpRsqn) 1~10
            for (int i = 23; i <= 32; i++) {
                askpRsqn.add(parseInt(fields[i]));
            }

            // 매수호가 잔량 (bidRsqn) 1~10
            for (int i = 33; i <= 42; i++) {
                bidRsqn.add(parseInt(fields[i]));
            }

            return GetRealTimeQuotesResponseDto.builder()
                    .stockCode(stockCode)
                    .askp(askp)
                    .bidp(bidp)
                    .askpRsqn(askpRsqn)
                    .bidRsqn(bidRsqn)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Long parseLong(String s) {
        try {
            return (s != null && !s.isBlank()) ? Long.parseLong(s) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer parseInt(String s) {
        try {
            return (s != null && !s.isBlank()) ? Integer.parseInt(s) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
