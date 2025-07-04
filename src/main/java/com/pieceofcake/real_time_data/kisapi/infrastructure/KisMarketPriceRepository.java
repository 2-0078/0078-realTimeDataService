package com.pieceofcake.real_time_data.kisapi.infrastructure;

import com.pieceofcake.real_time_data.kisapi.entity.KisMarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KisMarketPriceRepository extends JpaRepository<KisMarketPrice, Long> {
}
