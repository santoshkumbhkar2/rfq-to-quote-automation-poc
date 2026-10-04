package com.automation.rfq.repository;

import com.automation.rfq.domain.Rfq;
import com.automation.rfq.domain.RfqStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RfqRepository extends JpaRepository<Rfq, UUID> {
    Optional<Rfq> findByRfqNumber(String rfqNumber);

    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    Optional<Rfq> findWithDetailsById(UUID id);

    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    List<Rfq> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"customer", "items", "items.product"})
    List<Rfq> findByStatusOrderByCreatedAtDesc(RfqStatus status);
}
