package com.automation.rfq.dto;

import com.automation.rfq.domain.RfqStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RfqResponse(
        UUID id,
        String rfqNumber,
        UUID customerId,
        String customerName,
        RfqStatus status,
        String currency,
        List<RfqItemResponse> items,
        OffsetDateTime createdAt
) {}
