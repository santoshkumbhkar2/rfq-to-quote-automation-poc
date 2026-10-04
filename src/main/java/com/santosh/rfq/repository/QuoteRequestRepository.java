package com.santosh.rfq.repository;

import com.santosh.rfq.entity.QuoteRequest;
import com.santosh.rfq.entity.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for QuoteRequest.
 *
 * Golden Rule: Extending JpaRepository provides CRUD, pagination, and sorting
 * out of the box. Spring dynamically generates the SQL implementation at runtime!
 */
@Repository
public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, Long> {

    // Derived Query Method: Spring creates "SELECT * FROM quote_requests WHERE customer_email = ?"
    List<QuoteRequest> findByCustomerEmailIgnoreCase(String customerEmail);

    // Derived Query Method with Status
    List<QuoteRequest> findByStatus(QuoteStatus status);

    // Pagination support
    Page<QuoteRequest> findByStatus(QuoteStatus status, Pageable pageable);

    // Custom JPQL Query Example
    @Query("SELECT q FROM QuoteRequest q WHERE LOWER(q.partNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<QuoteRequest> searchByPartNumberKeyword(@Param("keyword") String keyword);
}
