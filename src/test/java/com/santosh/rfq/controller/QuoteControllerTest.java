package com.santosh.rfq.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santosh.rfq.dto.request.CreateQuoteRequest;
import com.santosh.rfq.dto.response.QuoteResponse;
import com.santosh.rfq.entity.QuoteStatus;
import com.santosh.rfq.service.QuoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller Slice Test using @WebMvcTest.
 *
 * Golden Rule of Web Slice Testing:
 * @WebMvcTest only initializes the Spring MVC infrastructure (Controllers, Filters,
 * ExceptionHandler, Converters) without spinning up database or full application context.
 * We mock the QuoteService to verify HTTP status codes, routing, and JSON serialization.
 */
@WebMvcTest(QuoteController.class)
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuoteService quoteService;

    @Test
    @DisplayName("POST /api/v1/quotes should return 201 Created when payload is valid")
    void createQuote_ValidPayload_Returns201() throws Exception {
        CreateQuoteRequest request = new CreateQuoteRequest(
                "buyer@aviation.com",
                "TURBINE-BLADE-01",
                10,
                new BigDecimal("1200.00"),
                "Rush delivery"
        );

        QuoteResponse response = new QuoteResponse(
                1L,
                "buyer@aviation.com",
                "TURBINE-BLADE-01",
                10,
                new BigDecimal("1200.00"),
                new BigDecimal("12000.00"),
                "Rush delivery",
                QuoteStatus.PENDING,
                LocalDateTime.now()
        );

        when(quoteService.createQuote(any(CreateQuoteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customerEmail").value("buyer@aviation.com"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/v1/quotes should return 400 Bad Request when validation fails")
    void createQuote_InvalidPayload_Returns400() throws Exception {
        // Customer email is invalid and quantity is 0 (violates @Min(1))
        CreateQuoteRequest invalidRequest = new CreateQuoteRequest(
                "not-an-email",
                "",
                0,
                new BigDecimal("-5.00"),
                null
        );

        mockMvc.perform(post("/api/v1/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.validationErrors.customerEmail").exists())
                .andExpect(jsonPath("$.validationErrors.partNumber").exists())
                .andExpect(jsonPath("$.validationErrors.quantity").exists());
    }
}
