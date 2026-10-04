package com.automation.rfq.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateRfqRequest(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        String currency
) {}
