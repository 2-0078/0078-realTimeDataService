package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.entity.KisMarketPrice;
import com.pieceofcake.real_time_data.kisapi.entity.KisQuotes;
import com.pieceofcake.real_time_data.kisapi.infrastructure.KisMarketPriceRepository;
import com.pieceofcake.real_time_data.kisapi.infrastructure.KisQuotesRepository;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeQuotesResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KisEntityServiceImpl implements KisEntityService {
    private final KisMarketPriceRepository kisMarketPriceRepository;
    private final KisQuotesRepository kisQuotesRepository;

    @Transactional
    @Override
    public Flux<KisMarketPrice> saveKisMarketPrice(List<GetRealTimeMarketPriceResponseDto> dto){
        return kisMarketPriceRepository.saveAll(dto.stream().map(GetRealTimeMarketPriceResponseDto::toEntity).toList());
    }

    @Transactional
    @Override
    public Mono<KisQuotes> saveKisQuotesInfo(GetRealTimeQuotesResponseDto dto) {
        return kisQuotesRepository.save(dto.toEntity());
    }
}
