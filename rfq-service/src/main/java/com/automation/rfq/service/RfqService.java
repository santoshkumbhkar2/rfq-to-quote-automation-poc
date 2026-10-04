package com.automation.rfq.service;

import com.automation.rfq.domain.*;
import com.automation.rfq.dto.AddRfqItemRequest;
import com.automation.rfq.dto.CreateRfqRequest;
import com.automation.rfq.dto.RfqItemResponse;
import com.automation.rfq.dto.RfqResponse;
import com.automation.rfq.exception.BusinessRuleViolationException;
import com.automation.rfq.exception.ResourceNotFoundException;
import com.automation.rfq.repository.CustomerRepository;
import com.automation.rfq.repository.ProductRepository;
import com.automation.rfq.repository.RfqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RfqService {

    private final RfqRepository rfqRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final WorkflowEventService workflowEventService;

    @Transactional
    public RfqResponse createRfq(CreateRfqRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.customerId()));

        String rfqNumber = "RFQ-" + System.currentTimeMillis() % 1000000;
        String currency = (request.currency() != null && !request.currency().isBlank()) ? request.currency() : "USD";

        Rfq rfq = Rfq.builder()
                .rfqNumber(rfqNumber)
                .customer(customer)
                .status(RfqStatus.SUBMITTED)
                .currency(currency)
                .build();

        Rfq savedRfq = rfqRepository.save(rfq);

        workflowEventService.recordEvent(
                "RFQ",
                savedRfq.getId(),
                "RFQ_CREATED",
                "Created RFQ #" + savedRfq.getRfqNumber() + " for customer " + customer.getName()
        );

        return mapToResponse(savedRfq);
    }

    @Transactional
    public RfqResponse addRfqItem(UUID rfqId, AddRfqItemRequest request) {
        Rfq rfq = rfqRepository.findWithDetailsById(rfqId)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ not found with ID: " + rfqId));

        if (rfq.getStatus() == RfqStatus.QUOTED || rfq.getStatus() == RfqStatus.CANCELLED) {
            throw new BusinessRuleViolationException("Cannot add items to RFQ in " + rfq.getStatus() + " status");
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.productId()));

        RfqItem item = RfqItem.builder()
                .product(product)
                .quantity(request.quantity())
                .requestedSpecs(request.requestedSpecs())
                .build();

        rfq.addItem(item);
        Rfq updatedRfq = rfqRepository.save(rfq);

        workflowEventService.recordEvent(
                "RFQ",
                updatedRfq.getId(),
                "ITEM_ADDED",
                "Added product '" + product.getName() + "' (Qty: " + request.quantity() + ") to RFQ #" + updatedRfq.getRfqNumber()
        );

        return mapToResponse(updatedRfq);
    }

    @Transactional(readOnly = true)
    public RfqResponse getRfqById(UUID id) {
        Rfq rfq = rfqRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ not found with ID: " + id));
        return mapToResponse(rfq);
    }

    @Transactional(readOnly = true)
    public List<RfqResponse> getAllRfqs(RfqStatus status) {
        List<Rfq> rfqs = (status != null)
                ? rfqRepository.findByStatusOrderByCreatedAtDesc(status)
                : rfqRepository.findAllByOrderByCreatedAtDesc();

        return rfqs.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public RfqResponse cancelRfq(UUID id, String reason) {
        Rfq rfq = rfqRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ not found with ID: " + id));

        if (rfq.getStatus() == RfqStatus.QUOTED) {
            throw new BusinessRuleViolationException("Cannot cancel an RFQ that has already been quoted");
        }

        rfq.setStatus(RfqStatus.CANCELLED);
        Rfq savedRfq = rfqRepository.save(rfq);

        String detail = (reason != null && !reason.isBlank()) ? " Reason: " + reason : "";
        workflowEventService.recordEvent(
                "RFQ",
                savedRfq.getId(),
                "RFQ_CANCELLED",
                "RFQ #" + savedRfq.getRfqNumber() + " was cancelled." + detail
        );

        return mapToResponse(savedRfq);
    }

    public Rfq getRfqEntityById(UUID id) {
        return rfqRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RFQ not found with ID: " + id));
    }

    public RfqResponse mapToResponse(Rfq rfq) {
        var itemResponses = rfq.getItems().stream()
                .map(item -> new RfqItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getSku(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getRequestedSpecs()
                ))
                .toList();

        return new RfqResponse(
                rfq.getId(),
                rfq.getRfqNumber(),
                rfq.getCustomer().getId(),
                rfq.getCustomer().getName(),
                rfq.getStatus(),
                rfq.getCurrency(),
                itemResponses,
                rfq.getCreatedAt()
        );
    }
}
