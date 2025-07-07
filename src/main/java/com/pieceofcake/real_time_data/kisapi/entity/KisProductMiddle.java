package com.pieceofcake.real_time_data.kisapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class KisProductMiddle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String pieceProductUuid;
    private String stockCode;

    @Builder
    public KisProductMiddle(Long id, String pieceProductUuid, String stockCode) {
        this.id = id;
        this.pieceProductUuid = pieceProductUuid;
        this.stockCode = stockCode;
    }
}
