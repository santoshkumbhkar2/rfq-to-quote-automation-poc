package com.automation.rfq.repository;

import com.automation.rfq.domain.PriceListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriceListItemRepository extends JpaRepository<PriceListItem, UUID> {

    @Query("SELECT pli FROM PriceListItem pli WHERE pli.priceList.id = :priceListId AND pli.product.id = :productId")
    Optional<PriceListItem> findByPriceListIdAndProductId(@Param("priceListId") UUID priceListId, @Param("productId") UUID productId);
}
