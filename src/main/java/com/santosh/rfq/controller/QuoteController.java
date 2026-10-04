package com.santosh.rfq.controller;

import com.santosh.rfq.dto.request.CreateQuoteRequest;
import com.santosh.rfq.dto.response.ErrorResponse;
import com.santosh.rfq.dto.response.QuoteResponse;
import com.santosh.rfq.entity.QuoteStatus;
import com.santosh.rfq.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing RFQ / Quote management endpoints.
 *
 * Golden Rules Applied:
 * 1. Controller is thin: It does NOT do business logic or DB calls directly.
 * 2. @Valid on request bodies enforces fail-fast input validation.
 * 3. Returns standard HTTP status codes (201 Created, 200 OK, 204 No Content).
 */
@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
@Tag(name = "Quote Management", description = "APIs for submitting, querying, and updating RFQ Quote Requests")
public class QuoteController {

    private final QuoteService quoteService;

    @Operation(summary = "Submit a new Request for Quote (RFQ)", description = "Creates a new Quote Request in PENDING status")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Quote created successfully",
                content = @Content(schema = @Schema(implementation = QuoteResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failure or invalid payload",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<QuoteResponse> createQuote(@Valid @RequestBody CreateQuoteRequest request) {
        QuoteResponse response = quoteService.createQuote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get quote by ID", description = "Fetches details of a specific quote by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quote found"),
        @ApiResponse(responseCode = "404", description = "Quote not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> getQuoteById(@PathVariable Long id) {
        QuoteResponse response = quoteService.getQuoteById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Query quotes by customer email", description = "Returns all quotes submitted by a specific customer")
    @GetMapping(params = "email")
    public ResponseEntity<List<QuoteResponse>> getQuotesByEmail(@RequestParam String email) {
        return ResponseEntity.ok(quoteService.getQuotesByCustomerEmail(email));
    }

    @Operation(summary = "Query quotes by status", description = "Returns all quotes filtered by their lifecycle status")
    @GetMapping(params = "status")
    public ResponseEntity<List<QuoteResponse>> getQuotesByStatus(@RequestParam QuoteStatus status) {
        return ResponseEntity.ok(quoteService.getQuotesByStatus(status));
    }

    @Operation(summary = "Update quote status", description = "Transitions the status of a quote (e.g. APPROVED, REJECTED)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<QuoteResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam QuoteStatus newStatus) {
        QuoteResponse updated = quoteService.updateQuoteStatus(id, newStatus);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete quote", description = "Removes a quote record by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        quoteService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }
}
