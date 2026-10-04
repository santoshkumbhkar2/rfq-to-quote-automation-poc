package com.automation.rfq.service;

import com.automation.rfq.domain.*;
import com.automation.rfq.dto.GenerateQuoteRequest;
import com.automation.rfq.dto.QuoteItemResponse;
import com.automation.rfq.dto.QuoteResponse;
import com.automation.rfq.exception.BusinessRuleViolationException;
import com.automation.rfq.exception.ResourceNotFoundException;
import com.automation.rfq.repository.QuoteRepository;
import com.automation.rfq.repository.RfqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final RfqRepository rfqRepository;
    private final PricingEngineService pricingEngineService;
    private final WorkflowEventService workflowEventService;

    @Transactional
    public QuoteResponse generateQuote(GenerateQuoteRequest request) {
        Rfq rfq = rfqRepository.findWithDetailsById(request.rfqId())
                .orElseThrow(() -> new ResourceNotFoundException("RFQ not found with ID: " + request.rfqId()));

        if (rfq.getItems().isEmpty()) {
            throw new BusinessRuleViolationException("Cannot generate quote for an empty RFQ. RFQ must contain at least one item.");
        }

        String quoteNumber = "QT-" + System.currentTimeMillis() % 1000000;
        BigDecimal taxRate = (request.taxRate() != null) ? request.taxRate() : new BigDecimal("0.1800");

        Quote quote = Quote.builder()
                .quoteNumber(quoteNumber)
                .rfq(rfq)
                .status(QuoteStatus.GENERATED)
                .currency(rfq.getCurrency())
                .taxRate(taxRate)
                .validUntil(OffsetDateTime.now().plusDays(30))
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (RfqItem rfqItem : rfq.getItems()) {
            Product product = rfqItem.getProduct();
            BigDecimal unitPrice = pricingEngineService.getUnitPriceForProduct(product, rfq.getCurrency());
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(rfqItem.getQuantity()))
                    .setScale(4, RoundingMode.HALF_UP);

            subtotal = subtotal.add(totalPrice);

            QuoteItem quoteItem = QuoteItem.builder()
                    .rfqItem(rfqItem)
                    .product(product)
                    .quantity(rfqItem.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .build();

            quote.addItem(quoteItem);
        }

        BigDecimal taxAmount = subtotal.multiply(taxRate).setScale(4, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount).setScale(4, RoundingMode.HALF_UP);

        quote.setSubtotal(subtotal);
        quote.setTaxAmount(taxAmount);
        quote.setTotalAmount(totalAmount);

        Quote savedQuote = quoteRepository.save(quote);

        // Update RFQ status
        rfq.setStatus(RfqStatus.QUOTED);
        rfqRepository.save(rfq);

        // Record Audit Event
        workflowEventService.recordEvent(
                "QUOTE",
                savedQuote.getId(),
                "QUOTE_GENERATED",
                "Generated Quote #" + savedQuote.getQuoteNumber() + " for RFQ #" + rfq.getRfqNumber() +
                        " with Subtotal: $" + subtotal + ", Tax: $" + taxAmount + ", Total: $" + totalAmount
        );

        return mapToResponse(savedQuote);
    }

    @Transactional(readOnly = true)
    public QuoteResponse getQuoteById(UUID id) {
        Quote quote = quoteRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));
        return mapToResponse(quote);
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> getAllQuotes() {
        return quoteRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<QuoteResponse> getQuotesByRfqId(UUID rfqId) {
        return quoteRepository.findWithDetailsByRfqId(rfqId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public QuoteResponse acceptQuote(UUID id) {
        Quote quote = quoteRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));

        if (quote.getStatus() == QuoteStatus.REJECTED) {
            throw new BusinessRuleViolationException("Cannot accept a quote that has been rejected");
        }

        quote.setStatus(QuoteStatus.ACCEPTED);
        Quote savedQuote = quoteRepository.save(quote);

        workflowEventService.recordEvent(
                "QUOTE",
                savedQuote.getId(),
                "QUOTE_ACCEPTED",
                "Quote #" + savedQuote.getQuoteNumber() + " was marked as ACCEPTED"
        );

        return mapToResponse(savedQuote);
    }

    @Transactional
    public QuoteResponse rejectQuote(UUID id, String reason) {
        Quote quote = quoteRepository.findWithDetailsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));

        if (quote.getStatus() == QuoteStatus.ACCEPTED) {
            throw new BusinessRuleViolationException("Cannot reject an already accepted quote");
        }

        quote.setStatus(QuoteStatus.REJECTED);
        Quote savedQuote = quoteRepository.save(quote);

        String detail = (reason != null && !reason.isBlank()) ? " Reason: " + reason : "";
        workflowEventService.recordEvent(
                "QUOTE",
                savedQuote.getId(),
                "QUOTE_REJECTED",
                "Quote #" + savedQuote.getQuoteNumber() + " was marked as REJECTED." + detail
        );

        return mapToResponse(savedQuote);
    }

    public QuoteResponse mapToResponse(Quote quote) {
        var itemResponses = quote.getItems().stream()
                .map(item -> new QuoteItemResponse(
                        item.getId(),
                        item.getProduct().getId(),
                        item.getProduct().getSku(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .toList();

        return new QuoteResponse(
                quote.getId(),
                quote.getQuoteNumber(),
                quote.getRfq().getId(),
                quote.getRfq().getRfqNumber(),
                quote.getStatus(),
                quote.getCurrency(),
                quote.getSubtotal(),
                quote.getTaxRate(),
                quote.getTaxAmount(),
                quote.getTotalAmount(),
                itemResponses,
                quote.getValidUntil(),
                quote.getCreatedAt()
        );
    }
}
