package com.pieceofcake.real_time_data.kisapi.infrastructure;

import com.pieceofcake.real_time_data.kisapi.entity.KisProductMiddle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KisProductMiddleRepository extends JpaRepository<KisProductMiddle, Long> {
    List<KisProductMiddle> findAllByPieceProductUuid(String pieceProductUuid);
}
