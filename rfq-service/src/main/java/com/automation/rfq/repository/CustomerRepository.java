package com.automation.rfq.repository;

import com.automation.rfq.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCompanyCode(String companyCode);
    Optional<Customer> findByEmail(String email);
}
