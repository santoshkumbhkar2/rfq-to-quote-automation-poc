package com.santosh.rfq.service.impl;

import com.santosh.rfq.dto.request.CreateQuoteRequest;
import com.santosh.rfq.dto.response.QuoteResponse;
import com.santosh.rfq.entity.QuoteRequest;
import com.santosh.rfq.entity.QuoteStatus;
import com.santosh.rfq.exception.ResourceNotFoundException;
import com.santosh.rfq.repository.QuoteRequestRepository;
import com.santosh.rfq.service.QuoteService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Concrete implementation of the QuoteService.
 *
 * Golden Rules Applied:
 * 1. Constructor Injection: Using @RequiredArgsConstructor instead of @Autowired on fields.
 * 2. @Transactional: Demarcates transaction boundaries. Automatically rolls back on RuntimeException.
 * 3. readOnly = true: Informs Hibernate not to do dirty checking on read operations, boosting query performance.
 * 4. Stateless: No mutable instance state stored on this bean.
 */
@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {

    private static final Logger log = LoggerFactory.getLogger(QuoteServiceImpl.class);

    private final QuoteRequestRepository quoteRepository;

    @Override
    @Transactional
    public QuoteResponse createQuote(CreateQuoteRequest request) {
        log.info("Creating new QuoteRequest for customer: {}, part: {}",
                request.customerEmail(), request.partNumber());

        QuoteRequest entity = QuoteRequest.builder()
                .customerEmail(request.customerEmail().trim().toLowerCase())
                .partNumber(request.partNumber().trim().toUpperCase())
                .quantity(request.quantity())
                .targetUnitPrice(request.targetUnitPrice())
                .notes(request.notes())
                .status(QuoteStatus.PENDING)
                .build();

        QuoteRequest saved = quoteRepository.save(entity);
        log.info("Successfully created QuoteRequest with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public QuoteResponse getQuoteById(Long id) {
        log.debug("Fetching QuoteRequest with ID: {}", id);
        QuoteRequest entity = findEntityById(id);
        return mapToResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteResponse> getQuotesByCustomerEmail(String email) {
        log.debug("Fetching QuoteRequests for customer email: {}", email);
        return quoteRepository.findByCustomerEmailIgnoreCase(email.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuoteResponse> getQuotesByStatus(QuoteStatus status) {
        log.debug("Fetching QuoteRequests with status: {}", status);
        return quoteRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuoteResponse updateQuoteStatus(Long id, QuoteStatus newStatus) {
        log.info("Updating status of QuoteRequest ID: {} to {}", id, newStatus);
        QuoteRequest entity = findEntityById(id);
        entity.setStatus(newStatus);
        // In @Transactional, entity changes are automatically flushed (dirty checking)
        QuoteRequest updated = quoteRepository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteQuote(Long id) {
        log.info("Deleting QuoteRequest ID: {}", id);
        QuoteRequest entity = findEntityById(id);
        quoteRepository.delete(entity);
    }

    private QuoteRequest findEntityById(Long id) {
        return quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuoteRequest", "id", id));
    }

    /**
     * Maps an internal database Entity to an external Response DTO.
     * Computes derived business values like totalEstimatedCost.
     */
    private QuoteResponse mapToResponse(QuoteRequest entity) {
        BigDecimal totalCost = null;
        if (entity.getTargetUnitPrice() != null && entity.getQuantity() != null) {
            totalCost = entity.getTargetUnitPrice().multiply(BigDecimal.valueOf(entity.getQuantity()));
        }

        return new QuoteResponse(
                entity.getId(),
                entity.getCustomerEmail(),
                entity.getPartNumber(),
                entity.getQuantity(),
                entity.getTargetUnitPrice(),
                totalCost,
                entity.getNotes(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
