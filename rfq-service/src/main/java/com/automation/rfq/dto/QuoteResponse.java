package com.automation.rfq.dto;

import com.automation.rfq.domain.QuoteStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record QuoteResponse(
        UUID id,
        String quoteNumber,
        UUID rfqId,
        String rfqNumber,
        QuoteStatus status,
        String currency,
        BigDecimal subtotal,
        BigDecimal taxRate,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        List<QuoteItemResponse> items,
        OffsetDateTime validUntil,
        OffsetDateTime createdAt
) {}
