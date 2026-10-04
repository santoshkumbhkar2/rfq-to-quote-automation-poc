package com.santosh.rfq.service;

import com.santosh.rfq.dto.request.CreateQuoteRequest;
import com.santosh.rfq.dto.response.QuoteResponse;
import com.santosh.rfq.entity.QuoteRequest;
import com.santosh.rfq.entity.QuoteStatus;
import com.santosh.rfq.exception.ResourceNotFoundException;
import com.santosh.rfq.repository.QuoteRequestRepository;
import com.santosh.rfq.service.impl.QuoteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for QuoteServiceImpl.
 *
 * Golden Rule of Unit Testing:
 * Test the business logic in pure isolation! Notice we do NOT use @SpringBootTest here;
 * instead, @ExtendWith(MockitoExtension.class) runs in milliseconds without spinning
 * up Tomcat or the Spring Container.
 */
@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRequestRepository quoteRepository;

    @InjectMocks
    private QuoteServiceImpl quoteService;

    @Test
    @DisplayName("createQuote() should save entity and return mapped response")
    void createQuote_Success() {
        // Given
        CreateQuoteRequest request = new CreateQuoteRequest(
                "buyer@aviation.com",
                "TURBINE-BLADE-01",
                10,
                new BigDecimal("1200.00"),
                "Rush delivery"
        );

        QuoteRequest savedEntity = QuoteRequest.builder()
                .id(1L)
                .customerEmail("buyer@aviation.com")
                .partNumber("TURBINE-BLADE-01")
                .quantity(10)
                .targetUnitPrice(new BigDecimal("1200.00"))
                .notes("Rush delivery")
                .status(QuoteStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        when(quoteRepository.save(any(QuoteRequest.class))).thenReturn(savedEntity);

        // When
        QuoteResponse response = quoteService.createQuote(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.customerEmail()).isEqualTo("buyer@aviation.com");
        assertThat(response.partNumber()).isEqualTo("TURBINE-BLADE-01");
        assertThat(response.quantity()).isEqualTo(10);
        assertThat(response.status()).isEqualTo(QuoteStatus.PENDING);
        assertThat(response.totalEstimatedCost()).isEqualByComparingTo(new BigDecimal("12000.00"));

        verify(quoteRepository, times(1)).save(any(QuoteRequest.class));
    }

    @Test
    @DisplayName("getQuoteById() should throw ResourceNotFoundException when quote does not exist")
    void getQuoteById_NotFound() {
        // Given
        when(quoteRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> quoteService.getQuoteById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("QuoteRequest not found with id : '999'");

        verify(quoteRepository, times(1)).findById(999L);
    }
}
