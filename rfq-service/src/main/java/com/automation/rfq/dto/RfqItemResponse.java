package com.automation.rfq.dto;

import java.util.UUID;

public record RfqItemResponse(
        UUID id,
        UUID productId,
        String productSku,
        String productName,
        Integer quantity,
        String requestedSpecs
) {}
