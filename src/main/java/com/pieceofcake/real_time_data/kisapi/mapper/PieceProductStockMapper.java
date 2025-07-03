package com.pieceofcake.real_time_data.kisapi.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class PieceProductStockMapper {

    private final Map<String, SubscriptionCounter> productSubscriberCounts = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> stockToProductMap = new ConcurrentHashMap<>();
    private final Map<String, String> productToStockMap = new ConcurrentHashMap<>();
    private final List<String> stockCodePool = List.of(
            "005930", "000660", "373220", "207940", "005380",
            "005935", "000270", "068270", "005490", "105560");

    public String getOrAssignStockCodeForSingleQuery(String productUuid) {
        String stockCode = productToStockMap.computeIfAbsent(productUuid, uuid -> {
            int idx = ThreadLocalRandom.current().nextInt(stockCodePool.size());
            String assignedStockCode = stockCodePool.get(idx);
            stockToProductMap.computeIfAbsent(assignedStockCode, k -> ConcurrentHashMap.newKeySet())
                    .add(productUuid);
            // 단일조회 전용 매핑은 구독자 카운터는 건드리지 않음
            scheduleMappingRemoval(productUuid, assignedStockCode, Duration.ofMinutes(60));

            log.info("🆕 단일조회용 매핑 생성 및 TTL 설정 - {} -> {} (60분 후 제거 예정)", productUuid, assignedStockCode);
            return assignedStockCode;
        });
        return stockCode;
    }

    public String getOrAssignStockCode(String productUuid, boolean isQuotes) {
        String stockCode = productToStockMap.computeIfAbsent(productUuid, uuid -> {
            int idx = ThreadLocalRandom.current().nextInt(stockCodePool.size());
            String assignedStockCode = stockCodePool.get(idx);

            // 👉 역방향 매핑 추가
            stockToProductMap.computeIfAbsent(assignedStockCode, k -> ConcurrentHashMap.newKeySet())
                    .add(productUuid);

            return assignedStockCode;
        });

        // 구독자 수 증가
        SubscriptionCounter counter = productSubscriberCounts.computeIfAbsent(productUuid, k -> new SubscriptionCounter());
        if (isQuotes) {
            counter.quotesCount.incrementAndGet();
            log.info("✅ [호가] 구독자 증가 - {} quotesCount={}", productUuid, counter.quotesCount.get());
        } else {
            counter.marketCount.incrementAndGet();
            log.info("✅ [시세] 구독자 증가 - {} marketCount={}", productUuid, counter.marketCount.get());
        }
        return stockCode;
    }

    public void removeSubscriberForPieceUuid(String pieceUuid, boolean isQuotes) {
        SubscriptionCounter counter = productSubscriberCounts.get(pieceUuid);
        if (counter != null) {
            int remainingQuotes = counter.quotesCount.get();
            int remainingMarket = counter.marketCount.get();

            if (isQuotes) {
                remainingQuotes = counter.quotesCount.decrementAndGet();
            } else {
                remainingMarket = counter.marketCount.decrementAndGet();
            }

            log.info("🛑 구독자 감소 - {} quotesCount={}, marketCount={}", pieceUuid, remainingQuotes, remainingMarket);

            if (remainingQuotes <= 0 && remainingMarket <= 0) {
                String stockCode = productToStockMap.remove(pieceUuid);
                productSubscriberCounts.remove(pieceUuid);

                if (stockCode != null) {
                    Set<String> pieceUuids = stockToProductMap.get(stockCode);
                    if (pieceUuids != null) {
                        pieceUuids.remove(pieceUuid);
                        log.info("🗑 stockToProductMap에서 제거 완료 - pieceUuid: {} stockCode: {}", pieceUuid, stockCode);

                        if (pieceUuids.isEmpty()) {
                            stockToProductMap.remove(stockCode);
                            log.info("🗑 stockCode에 매핑된 productUuid 없음 → stockToProductMap 엔트리 제거 - stockCode: {}", stockCode);
                        }
                    }
                }

                log.info("🗑 모든 구독 해제되어 매핑 제거 완료 - pieceUuid: {}", pieceUuid);
            }
        } else {
            log.info("ℹ️ 구독자 기록 없음 - pieceUuid: {}", pieceUuid);
        }
    }

    public Set<String> getPieceProductUuidsByStockCode(String stockCode) {
        return stockToProductMap.getOrDefault(stockCode, Set.of());
    }

    private void scheduleMappingRemoval(String productUuid, String stockCode, Duration ttl) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            productToStockMap.remove(productUuid);
            Set<String> pieceUuids = stockToProductMap.get(stockCode);
            if (pieceUuids != null) {
                pieceUuids.remove(productUuid);
                if (pieceUuids.isEmpty()) {
                    stockToProductMap.remove(stockCode);
                }
            }
            log.info("⏰ TTL 도래로 단일조회용 매핑 제거 완료 - pieceUuid: {} stockCode: {}", productUuid, stockCode);
        }, ttl.toMillis(), TimeUnit.MILLISECONDS);
    }

    // 구독자 카운터를 quotes(호가) / market(시세)로 분리
    private static class SubscriptionCounter {
        AtomicInteger quotesCount = new AtomicInteger(0);
        AtomicInteger marketCount = new AtomicInteger(0);
    }
}

