package com.automation.rfq.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkflowEventResponse(
        UUID id,
        String entityType,
        UUID entityId,
        String eventType,
        String message,
        OffsetDateTime createdAt
) {}
