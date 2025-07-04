package com.pieceofcake.real_time_data.kisapi.application;

import com.pieceofcake.real_time_data.kisapi.infrastructure.KisMarketPriceRepository;
import com.pieceofcake.real_time_data.websocket.dto.GetRealTimeMarketPriceResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KisMarketPriceEntityServiceImpl implements KisMarketPriceEntityService{
    private final KisMarketPriceRepository kisMarketPriceRepository;

    @Transactional
    @Override
    public void saveKisMarketPrice(List<GetRealTimeMarketPriceResponseDto> dto){
        kisMarketPriceRepository.saveAll(dto.stream().map(GetRealTimeMarketPriceResponseDto::toEntity).toList());
    }
}
