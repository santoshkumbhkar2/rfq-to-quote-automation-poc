package com.santosh.rfq.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized API Error Response returned by GlobalExceptionHandler.
 */
@Schema(description = "Standardized error payload")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    @Schema(description = "HTTP Status code", example = "400")
    int status,

    @Schema(description = "Error title / reason phrase", example = "Bad Request")
    String error,

    @Schema(description = "Descriptive error message", example = "Quote not found with id: 99")
    String message,

    @Schema(description = "Field-level validation error map (if applicable)")
    Map<String, String> validationErrors,

    @Schema(description = "API endpoint path where error occurred", example = "/api/v1/quotes/99")
    String path,

    @Schema(description = "Timestamp of the error")
    LocalDateTime timestamp
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, null, path, LocalDateTime.now());
    }

    public static ErrorResponse ofValidation(int status, String error, String message, Map<String, String> errors, String path) {
        return new ErrorResponse(status, error, message, errors, path, LocalDateTime.now());
    }
}
