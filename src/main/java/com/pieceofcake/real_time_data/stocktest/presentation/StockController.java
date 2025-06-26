package com.pieceofcake.real_time_data.stocktest.presentation;

import com.pieceofcake.real_time_data.stocktest.application.KisStockServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {
    private final KisStockServiceImpl kisStockService;

    @GetMapping
    public void connectStockData(){
        kisStockService.connectStockQuoteData();
//        kisStockService.connectStockExecutionPriceData();
    }
}
