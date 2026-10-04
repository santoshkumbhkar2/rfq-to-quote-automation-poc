package com.santosh.rfq.dto.response;

import com.santosh.rfq.entity.QuoteStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO returned to API clients.
 * Decouples internal database entities from external API contracts.
 */
@Schema(description = "Quote Request response representation")
public record QuoteResponse(
    @Schema(description = "Unique Quote ID", example = "1")
    Long id,

    @Schema(description = "Customer email", example = "procurement@acmecorp.com")
    String customerEmail,

    @Schema(description = "Part number", example = "PART-7890-X")
    String partNumber,

    @Schema(description = "Quantity requested", example = "500")
    Integer quantity,

    @Schema(description = "Target unit price", example = "14.50")
    BigDecimal targetUnitPrice,

    @Schema(description = "Calculated total estimated cost (quantity * target price)", example = "7250.00")
    BigDecimal totalEstimatedCost,

    @Schema(description = "Engineering or delivery notes", example = "Need delivery within 30 days")
    String notes,

    @Schema(description = "Current lifecycle status of the quote", example = "PENDING")
    QuoteStatus status,

    @Schema(description = "Timestamp when RFQ was submitted")
    LocalDateTime createdAt
) {}
