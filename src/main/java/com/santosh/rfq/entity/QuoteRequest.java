package com.santosh.rfq.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity representing the 'quote_requests' database table.
 *
 * Golden Rule: Entities represent the internal database schema and should
 * NEVER be exposed directly through REST Controller endpoints. Always map to/from DTOs.
 */
@Entity
@Table(name = "quote_requests", indexes = {
    @Index(name = "idx_customer_email", columnList = "customer_email"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "part_number", nullable = false)
    private String partNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "target_unit_price", precision = 12, scale = 2)
    private BigDecimal targetUnitPrice;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuoteStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
