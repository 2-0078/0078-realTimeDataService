package com.pieceofcake.real_time_data.kisapi.application;

public interface KisRealTimeStockService {
    void connectStockQuoteData();
    void stopStockQuoteSubscription();
//    void connectStockExecutionPriceData();
}
