package com.automation.rfq.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record GenerateQuoteRequest(
        @NotNull(message = "RFQ ID is required")
        UUID rfqId,

        @DecimalMin(value = "0.0000", message = "Tax rate cannot be negative")
        @DecimalMax(value = "1.0000", message = "Tax rate cannot exceed 100%")
        BigDecimal taxRate
) {}
