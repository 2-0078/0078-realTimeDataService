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

        // 기존 매핑 키 삭제
        Set<String> existingKeys = redisTemplate.keys("stock:piece:*");
        Set<String> reverseKeys = redisTemplate.keys("piece:stock:*");

        int deletedCount = 0;
        if (existingKeys != null && !existingKeys.isEmpty()) {
            redisTemplate.delete(existingKeys);
            deletedCount += existingKeys.size();
        }

        if (reverseKeys != null && !reverseKeys.isEmpty()) {
            redisTemplate.delete(reverseKeys);
            deletedCount += reverseKeys.size();
        }

        log.info("🗑️ 기존 Redis 매핑 키 {}개 삭제 완료", deletedCount);

        // DB에서 매핑 정보 조회
        List<KisProductMiddle> mappings = kisProductMiddleRepository.findAll();
        int totalCount = 0;

        for (KisProductMiddle mapping : mappings) {
            String stockCode = mapping.getStockCode();
            String pieceProductUuid = mapping.getPieceProductUuid();

            // 정방향 저장: stock → piece(Set)
            redisTemplate.opsForSet().add("stock:piece:" + stockCode, pieceProductUuid);

            // 역방향 저장: piece → stock(String)
            redisTemplate.opsForValue().set("piece:stock:" + pieceProductUuid, stockCode);

            totalCount++;
        }

        log.info("✅ Redis 초기화 및 적재 완료: 총 {}개의 매핑 정보를 적재했습니다.", totalCount);
    }
}
