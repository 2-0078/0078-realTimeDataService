package com.pieceofcake.real_time_data.kisapi.infrastructure;

import com.pieceofcake.real_time_data.kisapi.entity.KisQuotes;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface KisQuotesRepository extends ReactiveMongoRepository<KisQuotes, String> {
    @Tailable
    @Query("{ 'stockCode' : ?0 }")
    Flux<KisQuotes> findByStockCode(String stockCode);
}
