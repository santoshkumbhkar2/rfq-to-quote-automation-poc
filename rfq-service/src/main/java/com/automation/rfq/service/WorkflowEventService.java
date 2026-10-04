package com.automation.rfq.service;

import com.automation.rfq.domain.WorkflowEvent;
import com.automation.rfq.dto.WorkflowEventResponse;
import com.automation.rfq.repository.WorkflowEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowEventService {

    private final WorkflowEventRepository workflowEventRepository;

    @Transactional
    public WorkflowEvent recordEvent(String entityType, UUID entityId, String eventType, String message) {
        WorkflowEvent event = WorkflowEvent.builder()
                .entityType(entityType)
                .entityId(entityId)
                .eventType(eventType)
                .message(message)
                .build();
        return workflowEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<WorkflowEventResponse> getEventsForEntity(String entityType, UUID entityId) {
        return workflowEventRepository.findByEntityTypeAndEntityIdOrderByCreatedAtAsc(entityType, entityId)
                .stream()
                .map(event -> new WorkflowEventResponse(
                        event.getId(),
                        event.getEntityType(),
                        event.getEntityId(),
                        event.getEventType(),
                        event.getMessage(),
                        event.getCreatedAt()
                ))
                .toList();
    }
}
