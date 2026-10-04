package com.automation.rfq.repository;

import com.automation.rfq.domain.Quote;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, UUID> {
    Optional<Quote> findByQuoteNumber(String quoteNumber);

    List<Quote> findByRfqId(UUID rfqId);

    @EntityGraph(attributePaths = {"rfq", "items", "items.product"})
    Optional<Quote> findWithDetailsById(UUID id);

    @EntityGraph(attributePaths = {"rfq", "items", "items.product"})
    List<Quote> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"rfq", "items", "items.product"})
    List<Quote> findWithDetailsByRfqId(UUID rfqId);
}
