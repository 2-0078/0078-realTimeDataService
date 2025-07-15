package com.pieceofcake.real_time_data.kisapi.application.sse;

import com.pieceofcake.real_time_data.kisapi.entity.KisMarketPrice;
import com.pieceofcake.real_time_data.kisapi.entity.KisQuotes;
import com.pieceofcake.real_time_data.kisapi.infrastructure.KisMarketPriceRepository;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisApiWebFluxEventServiceImpl implements KisApiWebFluxEventService{

    private final KisMarketPriceRepository kisMarketPriceRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    /**
     * pieceProductUuid 기준으로 과거 체결 내역 조회
     */
    @Override
    public Flux<GetRealTimeMarketPriceResponseDto> getMarketPricesByPieceProductUuid(String pieceProductUuid) {
        return resolveStockCode(pieceProductUuid)
                .flatMapMany(stockCode ->
                        kisMarketPriceRepository.findByStockCode(stockCode)
                                .map(GetRealTimeMarketPriceResponseDto::from)
                );
    }

    /**
     * pieceProductUuid 기준으로 실시간 체결 데이터 구독 (Change Stream)
     */
    @Override
    public Flux<GetRealTimeMarketPriceResponseDto> getNewMarketPricesByPieceProductUuid(String pieceProductUuid) {
        return resolveStockCode(pieceProductUuid)
                .flatMapMany(stockCode -> {
                    ChangeStreamOptions options = ChangeStreamOptions.builder()
                            .filter(Aggregation.newAggregation(
                                    Aggregation.match(Criteria.where("operationType").is("insert")),
                                    Aggregation.match(Criteria.where("fullDocument.stockCode").is(stockCode))
                            )).build();

                    return reactiveMongoTemplate.changeStream("kis_market_price", options, Document.class)
                            .map(ChangeStreamEvent::getBody)
                            .map(document -> {
                                KisMarketPrice entity = KisMarketPrice.builder()
                                        .id(document.get("_id", ObjectId.class).toString())
                                        .stockCode(document.getString("stockCode"))
                                        .startingPrice(document.getLong("startingPrice"))
                                        .maximumPrice(document.getLong("maximumPrice"))
                                        .minimumPrice(document.getLong("minimumPrice"))
                                        .currentPrice(document.getLong("currentPrice"))
                                        .tradeQuantity(document.getLong("tradeQuantity"))
                                        .date(document.getDate("date").toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                                        .build();

                                return GetRealTimeMarketPriceResponseDto.from(entity);
                            })
                            .sample(Duration.ofSeconds(1)); // 1초마다 가장 최근 값만 emit
                });
    }

    /**
     * pieceProductUuid 기준으로 실시간 체결 데이터 구독 (Change Stream)
     */
    @Override
    public Flux<GetRealTimeQuotesResponseDto> getNewQuotesByPieceProductUuid(String pieceProductUuid) {
        return resolveStockCode(pieceProductUuid)
                .flatMapMany(stockCode -> {
                    ChangeStreamOptions options = ChangeStreamOptions.builder()
                            .filter(Aggregation.newAggregation(
                                    Aggregation.match(Criteria.where("operationType").is("insert")),
                                    Aggregation.match(Criteria.where("fullDocument.stockCode").is(stockCode))
                            )).build();

                    return reactiveMongoTemplate.changeStream("kis_quotes_info", options, Document.class)
                            .map(ChangeStreamEvent::getBody)
                            .map(document -> {
                                // Object → KisHogaInfo → DTO
                                KisQuotes entity = KisQuotes.builder()
                                        .id(document.get("_id", ObjectId.class).toString())
                                        .stockCode(document.getString("stockCode"))
                                        .askp((List<Long>) document.get("askp"))
                                        .bidp((List<Long>) document.get("bidp"))
                                        .askpRsqn((List<Integer>) document.get("askpRsqn"))
                                        .bidRsqn((List<Integer>) document.get("bidRsqn"))
                                        .build();
                                return GetRealTimeQuotesResponseDto.from(entity);
                            })
                            .sample(Duration.ofSeconds(1)); // 1초마다 가장 최근 값만 emit;
                });
    }

    /**
     * Redis에서 pieceProductUuid → stockCode 변환
     */
    @Override
    public Mono<String> resolveStockCode(String pieceProductUuid) {
        String redisKey = "piece:stock:" + pieceProductUuid;
        return redisTemplate.opsForValue().get(redisKey)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Redis 매핑 없음: " + pieceProductUuid)));
    }
}
