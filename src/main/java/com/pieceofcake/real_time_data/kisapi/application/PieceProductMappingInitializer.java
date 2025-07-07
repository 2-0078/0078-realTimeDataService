package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.entity.KisProductMiddle;
import com.pieceofcake.real_time_data.kisapi.infrastructure.KisProductMiddleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PieceProductMappingInitializer {

    private final StringRedisTemplate redisTemplate;
    private final KisProductMiddleRepository kisProductMiddleRepository;

    @PostConstruct
    public void initializeMappings() {
        log.info("🔔 PieceProductMappingCache 초기화 시작");

        // 1) Redis에 stock:piece:* 키 존재 여부 확인
        Set<String> existingKeys = redisTemplate.keys("stock:piece:*");

        if (existingKeys != null && !existingKeys.isEmpty()) {
            // 2) 기존 매핑키가 있으면 삭제
            redisTemplate.delete(existingKeys);
            log.info("🗑️ 기존 Redis 매핑 키 {}개 삭제 완료", existingKeys.size());
        } else {
            log.info("✅ Redis에 기존 매핑 키 없음 → 초기 적재를 진행합니다.");
        }

        // 3) DB에서 stockCode ↔ pieceProductUuid 매핑 정보 조회
        List<KisProductMiddle> mappings = kisProductMiddleRepository.findAll();
        int totalCount = 0;

        for (KisProductMiddle mapping : mappings) {
            String stockCode = mapping.getStockCode();
            String pieceProductUuid = mapping.getPieceProductUuid();

            redisTemplate.opsForSet().add("stock:piece:" + stockCode, pieceProductUuid);
            totalCount++;
        }

        log.info("✅ Redis 초기화 및 적재 완료: 총 {}개의 매핑 정보를 적재했습니다.", totalCount);
    }
}
