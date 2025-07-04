package com.pieceofcake.real_time_data.kisapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class KisMarketPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String stockCode;   // 주식코드
    private Long startingPrice; // 시가
    private Long maximumPrice;  // 최고가
    private Long minimumPrice;  // 최저가
    private Long currentPrice;  // 현재가
    private Long tradeQuantity; // 체결 수량
    private LocalDateTime date;     // 체결 시간

    @Builder
    public KisMarketPrice(Long id, String stockCode, Long startingPrice, Long maximumPrice, Long minimumPrice,
                          Long currentPrice, Long tradeQuantity, LocalDateTime date) {
        this.id = id;
        this.stockCode = stockCode;
        this.startingPrice = startingPrice;
        this.maximumPrice = maximumPrice;
        this.minimumPrice = minimumPrice;
        this.currentPrice = currentPrice;
        this.tradeQuantity = tradeQuantity;
        this.date = date;
    }
}