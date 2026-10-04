package com.santosh.rfq.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new Quote Request.
 * Uses modern Java record for immutability and concise syntax.
 *
 * Golden Rule: Always validate incoming request payloads at the boundary!
 */
@Schema(description = "Payload to submit a new Request for Quote (RFQ)")
public record CreateQuoteRequest(

    @Schema(description = "Customer email address", example = "procurement@acmecorp.com")
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be a valid email address")
    String customerEmail,

    @Schema(description = "Manufacturer part number or SKU", example = "PART-7890-X")
    @NotBlank(message = "Part number is required")
    String partNumber,

    @Schema(description = "Quantity of parts required", example = "500")
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @Schema(description = "Target unit price budget in USD", example = "14.50")
    @Positive(message = "Target unit price must be greater than zero")
    BigDecimal targetUnitPrice,

    @Schema(description = "Optional engineering or delivery notes", example = "Need delivery within 30 days")
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    String notes
) {}
