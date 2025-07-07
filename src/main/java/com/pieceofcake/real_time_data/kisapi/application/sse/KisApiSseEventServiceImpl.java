package com.pieceofcake.real_time_data.kisapi.application.sse;

import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

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
        Sinks.Many<GetRealTimeQuotesResponseDto> sink = quotesSinks.computeIfAbsent(pieceProductUuid,
                k -> Sinks.many().multicast().onBackpressureBuffer());
        return sink.asFlux();
    }

    @Override
    public Flux<GetRealTimeMarketPriceResponseDto> getKisMatchedUpdatesByPieceProductUuid(String pieceProductUuid) {
        Sinks.Many<GetRealTimeMarketPriceResponseDto> sink = matchedSinks.computeIfAbsent(pieceProductUuid,
                k -> Sinks.many().multicast().onBackpressureBuffer());
        return sink.asFlux();
    }

    public void emitQuotesEvent(String pieceProductUuid, GetRealTimeQuotesResponseDto dto) {
        emitToSink(quotesSinks, pieceProductUuid, dto, "[KisApiSseEventService] 호가 SSE 송출");
    }

    public void emitMarketPriceEvent(String pieceProductUuid, GetRealTimeMarketPriceResponseDto dto) {
        emitToSink(matchedSinks, pieceProductUuid, dto, "[KisApiSseEventService] 체결 SSE 송출");
    }

    private <T> void emitToSink(Map<String, Sinks.Many<T>> sinks, String pieceProductUuid, T event, String logPrefix) {
        Sinks.Many<T> sink = sinks.get(pieceProductUuid);
        if (sink != null) {
            sink.tryEmitNext(event);
            log.info("{}: pieceProductUuid={}, event={}", logPrefix, pieceProductUuid, event);
        } else {
            log.warn("[KisApiSseEventService] sink 없음: pieceProductUuid={}, eventType={}", pieceProductUuid, event.getClass().getSimpleName());
        }
    }
}
