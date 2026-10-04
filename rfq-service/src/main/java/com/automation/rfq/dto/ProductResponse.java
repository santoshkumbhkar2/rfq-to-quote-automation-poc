package com.automation.rfq.dto;

import java.util.UUID;

public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        String unit,
        String category
) {}
