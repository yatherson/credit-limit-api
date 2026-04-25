package com.api.creditlimit.repository;

import com.api.creditlimit.domain.CreditLimitHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditLimitHistoryRepository extends JpaRepository<CreditLimitHistory, Long> {

    Page<CreditLimitHistory> findByCustomerId(Long customerId, Pageable pageable);
}