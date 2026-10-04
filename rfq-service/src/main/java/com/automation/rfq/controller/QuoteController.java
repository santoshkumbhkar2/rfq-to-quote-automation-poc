package com.automation.rfq.controller;

import com.automation.rfq.dto.GenerateQuoteRequest;
import com.automation.rfq.dto.QuoteResponse;
import com.automation.rfq.service.QuoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Quote Generation API", description = "Endpoints for generating and retrieving quotations")
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("/quotes")
    @Operation(summary = "List all quotes", description = "Retrieves all generated quotations")
    public ResponseEntity<List<QuoteResponse>> getAllQuotes() {
        return ResponseEntity.ok(quoteService.getAllQuotes());
    }

    @GetMapping("/rfqs/{rfqId}/quotes")
    @Operation(summary = "Get quotes for RFQ", description = "Retrieves all quotations generated for a specific RFQ")
    public ResponseEntity<List<QuoteResponse>> getQuotesByRfqId(@PathVariable UUID rfqId) {
        return ResponseEntity.ok(quoteService.getQuotesByRfqId(rfqId));
    }

    @PostMapping("/rfqs/{rfqId}/generate-quote")
    @Operation(summary = "Generate Quote from RFQ", description = "Executes pricing engine rules and generates a quotation for an RFQ")
    public ResponseEntity<QuoteResponse> generateQuote(
            @PathVariable UUID rfqId,
            @RequestBody(required = false) GenerateQuoteRequest request) {
        
        UUID targetRfqId = (request != null && request.rfqId() != null) ? request.rfqId() : rfqId;
        var finalRequest = new GenerateQuoteRequest(targetRfqId, (request != null) ? request.taxRate() : null);

        QuoteResponse response = quoteService.generateQuote(finalRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/quotes/{id}")
    @Operation(summary = "Get Quote by ID", description = "Retrieves complete details of a generated quotation")
    public ResponseEntity<QuoteResponse> getQuoteById(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.getQuoteById(id));
    }

    @PutMapping("/quotes/{id}/accept")
    @Operation(summary = "Accept Quote", description = "Marks a quotation as accepted by the customer")
    public ResponseEntity<QuoteResponse> acceptQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteService.acceptQuote(id));
    }

    @PutMapping("/quotes/{id}/reject")
    @Operation(summary = "Reject Quote", description = "Marks a quotation as rejected")
    public ResponseEntity<QuoteResponse> rejectQuote(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(quoteService.rejectQuote(id, reason));
    }
}
