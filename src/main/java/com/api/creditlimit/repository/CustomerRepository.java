package com.api.creditlimit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<com.suitpay.creditlimit.domain.Customer, Long> {
}