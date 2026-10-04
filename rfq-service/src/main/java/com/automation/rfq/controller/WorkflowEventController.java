package com.automation.rfq.controller;

import com.automation.rfq.dto.WorkflowEventResponse;
import com.automation.rfq.service.WorkflowEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Workflow Audit Log API", description = "Endpoints for inspecting audit history of RFQs and Quotes")
public class WorkflowEventController {

    private final WorkflowEventService workflowEventService;

    @GetMapping("/{entityType}/{entityId}")
    @Operation(summary = "Get audit trail for entity", description = "Retrieves all workflow events recorded for a given entity type (RFQ / QUOTE) and entity ID")
    public ResponseEntity<List<WorkflowEventResponse>> getEventsForEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId) {
        return ResponseEntity.ok(workflowEventService.getEventsForEntity(entityType, entityId));
    }
}
