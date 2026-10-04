package com.santosh.rfq.service;

import com.santosh.rfq.dto.request.CreateQuoteRequest;
import com.santosh.rfq.dto.response.QuoteResponse;
import com.santosh.rfq.entity.QuoteStatus;

import java.util.List;

/**
 * Service interface specifying the business capabilities of the Quote domain.
 * Coding to interfaces ensures clean architecture and effortless mocking in tests.
 */
public interface QuoteService {

    QuoteResponse createQuote(CreateQuoteRequest request);

    QuoteResponse getQuoteById(Long id);

    List<QuoteResponse> getQuotesByCustomerEmail(String email);

    List<QuoteResponse> getQuotesByStatus(QuoteStatus status);

    QuoteResponse updateQuoteStatus(Long id, QuoteStatus newStatus);

    void deleteQuote(Long id);
}
