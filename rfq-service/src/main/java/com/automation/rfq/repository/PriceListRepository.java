package com.automation.rfq.repository;

import com.automation.rfq.domain.PriceList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceListRepository extends JpaRepository<PriceList, UUID> {
    
    @Query("SELECT pl FROM PriceList pl WHERE pl.currency = :currency AND pl.effectiveFrom <= :date AND (pl.effectiveTo IS NULL OR pl.effectiveTo >= :date) ORDER BY pl.effectiveFrom DESC LIMIT 1")
    Optional<PriceList> findActivePriceList(@Param("currency") String currency, @Param("date") LocalDate date);
}
