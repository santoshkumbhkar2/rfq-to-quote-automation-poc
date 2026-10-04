package com.automation.rfq.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record QuoteItemResponse(
        UUID id,
        UUID productId,
        String productSku,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {}
