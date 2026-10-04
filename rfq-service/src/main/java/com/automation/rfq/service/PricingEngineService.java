package com.automation.rfq.service;

import com.automation.rfq.domain.PriceList;
import com.automation.rfq.domain.PriceListItem;
import com.automation.rfq.domain.Product;
import com.automation.rfq.exception.BusinessRuleViolationException;
import com.automation.rfq.repository.PriceListItemRepository;
import com.automation.rfq.repository.PriceListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PricingEngineService {

    private final PriceListRepository priceListRepository;
    private final PriceListItemRepository priceListItemRepository;

    @Transactional(readOnly = true)
    public BigDecimal getUnitPriceForProduct(Product product, String currency) {
        LocalDate today = LocalDate.now();

        PriceList activePriceList = priceListRepository.findActivePriceList(currency, today)
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "No active price list found for currency: " + currency + " on date: " + today));

        PriceListItem priceListItem = priceListItemRepository.findByPriceListIdAndProductId(activePriceList.getId(), product.getId())
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "Product '" + product.getName() + "' (SKU: " + product.getSku() + ") does not have a price entry in price list '" + activePriceList.getName() + "'"));

        return priceListItem.getUnitPrice();
    }
}
