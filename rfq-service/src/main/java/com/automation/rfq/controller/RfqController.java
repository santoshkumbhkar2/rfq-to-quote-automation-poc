package com.automation.rfq.controller;

import com.automation.rfq.domain.RfqStatus;
import com.automation.rfq.dto.AddRfqItemRequest;
import com.automation.rfq.dto.CreateRfqRequest;
import com.automation.rfq.dto.RfqResponse;
import com.automation.rfq.service.RfqService;
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
@RequestMapping("/api/v1/rfqs")
@RequiredArgsConstructor
@Tag(name = "RFQ Management API", description = "Endpoints for creating and managing Requests for Quotation (RFQs)")
public class RfqController {

    private final RfqService rfqService;

    @GetMapping
    @Operation(summary = "List all RFQs", description = "Retrieves all RFQs, optionally filtered by status")
    public ResponseEntity<List<RfqResponse>> getAllRfqs(
            @RequestParam(required = false) RfqStatus status) {
        return ResponseEntity.ok(rfqService.getAllRfqs(status));
    }

    @PostMapping
    @Operation(summary = "Create a new RFQ", description = "Creates a new RFQ for a customer")
    public ResponseEntity<RfqResponse> createRfq(@Valid @RequestBody CreateRfqRequest request) {
        RfqResponse response = rfqService.createRfq(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get RFQ by ID", description = "Retrieves complete details of an RFQ including its items")
    public ResponseEntity<RfqResponse> getRfqById(@PathVariable UUID id) {
        return ResponseEntity.ok(rfqService.getRfqById(id));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Add item to RFQ", description = "Adds a requested product and quantity to an existing RFQ")
    public ResponseEntity<RfqResponse> addRfqItem(
            @PathVariable UUID id,
            @Valid @RequestBody AddRfqItemRequest request) {
        RfqResponse response = rfqService.addRfqItem(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an RFQ", description = "Cancels an active RFQ before a quotation is generated")
    public ResponseEntity<RfqResponse> cancelRfq(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(rfqService.cancelRfq(id, reason));
    }
}
