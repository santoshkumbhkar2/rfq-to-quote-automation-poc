package com.automation.rfq.repository;

import com.automation.rfq.domain.WorkflowEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WorkflowEventRepository extends JpaRepository<WorkflowEvent, UUID> {
    List<WorkflowEvent> findByEntityTypeAndEntityIdOrderByCreatedAtAsc(String entityType, UUID entityId);
}
