package com.bank.common.customer_service.repository;

import com.bank.common.customer_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByAuthId(String authId);

    boolean existsByPesel(String pesel);

    boolean existsByAuthId(String authId);
}
