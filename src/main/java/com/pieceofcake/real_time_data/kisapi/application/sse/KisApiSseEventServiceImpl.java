package com.pieceofcake.real_time_data.kisapi.application.sse;

import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Scannable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KisApiSseEventServiceImpl implements KisApiSseEventService {

    private final Map<String, Sinks.Many<GetRealTimeQuotesResponseDto>> quotesSinks = new ConcurrentHashMap<>();
    private final Map<String, Sinks.Many<GetRealTimeMarketPriceResponseDto>> matchedSinks = new ConcurrentHashMap<>();

    @Override
    public Flux<GetRealTimeQuotesResponseDto> getKisQuotesUpdatesByPieceProductUuid(String pieceProductUuid) {
        return getOrCreateSinkFlux(quotesSinks, pieceProductUuid);
    }

    @Override
    public Flux<GetRealTimeMarketPriceResponseDto> getKisMatchedUpdatesByPieceProductUuid(String pieceProductUuid) {
        return getOrCreateSinkFlux(matchedSinks, pieceProductUuid);
    }

    public void emitQuotesEvent(String pieceProductUuid, GetRealTimeQuotesResponseDto dto) {
        emitToSink(quotesSinks, pieceProductUuid, dto, "[KisApiSseEventService] 호가 SSE 송출");
    }

    public void emitMarketPriceEvent(String pieceProductUuid, GetRealTimeMarketPriceResponseDto dto) {
        emitToSink(matchedSinks, pieceProductUuid, dto, "[KisApiSseEventService] 체결 SSE 송출");
    }

    private <T> Flux<T> getOrCreateSinkFlux(Map<String, Sinks.Many<T>> sinkMap, String pieceProductUuid) {
        Sinks.Many<T> sink = sinkMap.compute(pieceProductUuid, (k, existingSink) -> {
            if (existingSink == null || Boolean.TRUE.equals(existingSink.scan(Scannable.Attr.TERMINATED))) {
                log.info("[SSE] 새로운 sink 생성: pieceProductUuid={}", pieceProductUuid);
                return Sinks.many().replay().latest();  // ✅ replay.latest
            }
            return existingSink;
        });

        return sink.asFlux()
                .doOnSubscribe(sub -> log.info("[SSE] 구독 시작: pieceProductUuid={}", pieceProductUuid))
                .doOnCancel(() -> cleanupSinkIfNoSubscribers(sinkMap, pieceProductUuid, sink))
                .doOnComplete(() -> cleanupSinkIfNoSubscribers(sinkMap, pieceProductUuid, sink))
                .doOnError(e -> {
                    if (e instanceof IOException) {
                        log.info("[SSE] 클라이언트 연결 종료 감지: pieceProductUuid={}", pieceProductUuid);
                    } else {
                        log.error("[SSE] 구독 중 에러 발생: pieceProductUuid={}", pieceProductUuid, e);
                    }
                    cleanupSinkIfNoSubscribers(sinkMap, pieceProductUuid, sink);
                });
    }

    private <T> void cleanupSinkIfNoSubscribers(Map<String, Sinks.Many<T>> sinkMap,
                                                String pieceProductUuid,
                                                Sinks.Many<T> sink) {
        int subscribers = sink.currentSubscriberCount();
        boolean terminated = Boolean.TRUE.equals(sink.scan(Scannable.Attr.TERMINATED));

        log.info("[SSE] sink 상태 확인: pieceProductUuid={}, subscribers={}, terminated={}",
                pieceProductUuid, subscribers, terminated);

        if (subscribers == 0) {
            sinkMap.remove(pieceProductUuid, sink);
            log.info("[SSE] sink 제거 완료: pieceProductUuid={}", pieceProductUuid);
        }
    }

    private <T> void emitToSink(Map<String, Sinks.Many<T>> sinks,
                                String pieceProductUuid,
                                T event,
                                String logPrefix) {
        Sinks.Many<T> sink = sinks.get(pieceProductUuid);
        if (sink != null) {
            Sinks.EmitResult result = sink.tryEmitNext(event);
            if (result.isSuccess()) {
                log.info("{}: pieceProductUuid={}, event={}", logPrefix, pieceProductUuid, event);
            } else {
                log.warn("{} emit 실패: pieceProductUuid={}, result={}", logPrefix, pieceProductUuid, result);
            }
        } else {
            log.warn("[SSE] sink 없음: pieceProductUuid={}, eventType={}", pieceProductUuid, event.getClass().getSimpleName());
        }
    }
}
