package com.pieceofcake.real_time_data.stocktest.presentation;

import com.pieceofcake.real_time_data.kisapi.application.KisRealTimeKisRealTimeStockServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {
    private final KisRealTimeKisRealTimeStockServiceImpl kisStockService;

    @GetMapping
    public void connectStockData(){
//        kisStockService.connectStockQuoteData();
//        kisStockService.connectStockExecutionPriceData();
    }
}
