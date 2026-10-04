package com.automation.rfq.service;

import com.automation.rfq.domain.*;
import com.automation.rfq.dto.GenerateQuoteRequest;
import com.automation.rfq.dto.QuoteResponse;
import com.automation.rfq.exception.BusinessRuleViolationException;
import com.automation.rfq.repository.QuoteRepository;
import com.automation.rfq.repository.RfqRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private RfqRepository rfqRepository;

    @Mock
    private PricingEngineService pricingEngineService;

    @Mock
    private WorkflowEventService workflowEventService;

    @InjectMocks
    private QuoteService quoteService;

    private Customer customer;
    private Product product1;
    private Product product2;
    private Rfq rfq;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(UUID.randomUUID())
                .name("Test Customer")
                .email("test@customer.com")
                .companyCode("CUST-001")
                .build();

        product1 = Product.builder()
                .id(UUID.randomUUID())
                .sku("IND-SENS-100")
                .name("Industrial Sensor")
                .unit("EA")
                .build();

        product2 = Product.builder()
                .id(UUID.randomUUID())
                .sku("CTRL-MOD-020")
                .name("Control Module")
                .unit("EA")
                .build();

        rfq = Rfq.builder()
                .id(UUID.randomUUID())
                .rfqNumber("RFQ-10001")
                .customer(customer)
                .currency("USD")
                .status(RfqStatus.SUBMITTED)
                .build();
    }

    @Test
    @DisplayName("Should successfully generate quote with subtotal, tax, and total for valid RFQ")
    void shouldGenerateQuoteForValidRfq() {
        // Given
        RfqItem item1 = RfqItem.builder().id(UUID.randomUUID()).rfq(rfq).product(product1).quantity(100).build();
        RfqItem item2 = RfqItem.builder().id(UUID.randomUUID()).rfq(rfq).product(product2).quantity(20).build();
        rfq.addItem(item1);
        rfq.addItem(item2);

        when(rfqRepository.findWithDetailsById(rfq.getId())).thenReturn(Optional.of(rfq));
        when(pricingEngineService.getUnitPriceForProduct(product1, "USD")).thenReturn(new BigDecimal("150.0000"));
        when(pricingEngineService.getUnitPriceForProduct(product2, "USD")).thenReturn(new BigDecimal("450.0000"));

        when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> {
            Quote q = invocation.getArgument(0);
            q.setId(UUID.randomUUID());
            return q;
        });

        GenerateQuoteRequest request = new GenerateQuoteRequest(rfq.getId(), new BigDecimal("0.1800"));

        // When
        QuoteResponse response = quoteService.generateQuote(request);

        // Then
        // Item 1: 100 * 150.00 = 15,000.00
        // Item 2: 20 * 450.00  = 9,000.00
        // Subtotal = 24,000.00
        // Tax 18% = 4,320.00
        // Total = 28,320.00
        assertThat(response).isNotNull();
        assertThat(response.subtotal()).isEqualByComparingTo(new BigDecimal("24000.0000"));
        assertThat(response.taxAmount()).isEqualByComparingTo(new BigDecimal("4320.0000"));
        assertThat(response.totalAmount()).isEqualByComparingTo(new BigDecimal("28320.0000"));
        assertThat(response.items()).hasSize(2);

        verify(rfqRepository).save(rfq);
        verify(workflowEventService).recordEvent(eq("QUOTE"), any(UUID.class), eq("QUOTE_GENERATED"), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when attempting to generate quote for empty RFQ")
    void shouldThrowExceptionForEmptyRfq() {
        // Given
        rfq.setItems(Collections.emptyList());
        when(rfqRepository.findWithDetailsById(rfq.getId())).thenReturn(Optional.of(rfq));

        GenerateQuoteRequest request = new GenerateQuoteRequest(rfq.getId(), new BigDecimal("0.1800"));

        // When & Then
        assertThatThrownBy(() -> quoteService.generateQuote(request))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Cannot generate quote for an empty RFQ");

        verifyNoInteractions(pricingEngineService);
        verify(quoteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully accept a generated quote")
    void shouldAcceptQuote() {
        UUID quoteId = UUID.randomUUID();
        Quote quote = Quote.builder()
                .id(quoteId)
                .quoteNumber("QT-123456")
                .rfq(rfq)
                .status(QuoteStatus.GENERATED)
                .currency("USD")
                .subtotal(new BigDecimal("1000.0000"))
                .taxRate(new BigDecimal("0.1800"))
                .taxAmount(new BigDecimal("180.0000"))
                .totalAmount(new BigDecimal("1180.0000"))
                .build();

        when(quoteRepository.findWithDetailsById(quoteId)).thenReturn(Optional.of(quote));
        when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuoteResponse response = quoteService.acceptQuote(quoteId);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(QuoteStatus.ACCEPTED);
        verify(workflowEventService).recordEvent(eq("QUOTE"), eq(quoteId), eq("QUOTE_ACCEPTED"), anyString());
    }

    @Test
    @DisplayName("Should throw BusinessRuleViolationException when accepting an already rejected quote")
    void shouldThrowWhenAcceptingRejectedQuote() {
        UUID quoteId = UUID.randomUUID();
        Quote quote = Quote.builder()
                .id(quoteId)
                .quoteNumber("QT-123456")
                .rfq(rfq)
                .status(QuoteStatus.REJECTED)
                .currency("USD")
                .subtotal(new BigDecimal("1000.0000"))
                .taxRate(new BigDecimal("0.1800"))
                .taxAmount(new BigDecimal("180.0000"))
                .totalAmount(new BigDecimal("1180.0000"))
                .build();

        when(quoteRepository.findWithDetailsById(quoteId)).thenReturn(Optional.of(quote));

        assertThatThrownBy(() -> quoteService.acceptQuote(quoteId))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("Cannot accept a quote that has been rejected");

        verify(quoteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should successfully reject a quote with reason")
    void shouldRejectQuote() {
        UUID quoteId = UUID.randomUUID();
        Quote quote = Quote.builder()
                .id(quoteId)
                .quoteNumber("QT-123456")
                .rfq(rfq)
                .status(QuoteStatus.GENERATED)
                .currency("USD")
                .subtotal(new BigDecimal("1000.0000"))
                .taxRate(new BigDecimal("0.1800"))
                .taxAmount(new BigDecimal("180.0000"))
                .totalAmount(new BigDecimal("1180.0000"))
                .build();

        when(quoteRepository.findWithDetailsById(quoteId)).thenReturn(Optional.of(quote));
        when(quoteRepository.save(any(Quote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuoteResponse response = quoteService.rejectQuote(quoteId, "Pricing too high");

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(QuoteStatus.REJECTED);
        verify(workflowEventService).recordEvent(eq("QUOTE"), eq(quoteId), eq("QUOTE_REJECTED"), contains("Pricing too high"));
    }
}
