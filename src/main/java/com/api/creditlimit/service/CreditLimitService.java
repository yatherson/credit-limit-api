package com.api.creditlimit.service;

import com.api.creditlimit.domain.CreditLimitHistory;
import com.api.creditlimit.domain.Customer;
import com.api.creditlimit.domain.User;
import com.api.creditlimit.dto.CreditLimitHistoryResponse;
import com.api.creditlimit.dto.CreditLimitResponse;
import com.api.creditlimit.dto.UpdateCreditLimitRequest;
import com.api.creditlimit.dto.UpdateCreditLimitResponse;
import com.api.creditlimit.exception.CustomerNotFoundException;
import com.api.creditlimit.exception.NegativeCreditLimitException;
import com.api.creditlimit.exception.VipMinimumLimitException;
import com.api.creditlimit.repository.CreditLimitHistoryRepository;
import com.api.creditlimit.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class CreditLimitService {

    private final CustomerRepository customerRepository;
    private final CreditLimitHistoryRepository creditLimitHistoryRepository;

    @Value("${credit.limit.vip.minimum}")
    private BigDecimal vipMinimumLimit;

    public CreditLimitService(CustomerRepository customerRepository,
                              CreditLimitHistoryRepository creditLimitHistoryRepository) {
        this.customerRepository = customerRepository;
        this.creditLimitHistoryRepository = creditLimitHistoryRepository;
    }

    public CreditLimitResponse getCreditLimit(Long customerId) {
        Customer customer = findCustomerById(customerId);
        return CreditLimitResponse.from(customer);
    }

    @Transactional
    public UpdateCreditLimitResponse updateCreditLimit(Long customerId,
                                                       UpdateCreditLimitRequest request,
                                                       User loggedUser) {
        Customer customer = findCustomerById(customerId);

        validateNegativeLimit(request.newLimit());
        validateVipMinimumLimit(customer, request.newLimit());

        BigDecimal previousLimit = customer.getCreditLimit();

        customer.setCreditLimit(request.newLimit());
        customerRepository.save(customer);

        saveCreditLimitHistory(customer, loggedUser, previousLimit, request.newLimit());

        return UpdateCreditLimitResponse.from(customer, previousLimit);
    }

    public Page<CreditLimitHistoryResponse> getCreditLimitHistory(Long customerId, Pageable pageable) {
        findCustomerById(customerId);
        return creditLimitHistoryRepository
                .findByCustomerId(customerId, pageable)
                .map(CreditLimitHistoryResponse::from);
    }

    private Customer findCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }

    private void validateNegativeLimit(BigDecimal newLimit) {
        if (newLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeCreditLimitException();
        }
    }

    private void validateVipMinimumLimit(Customer customer, BigDecimal newLimit) {
        if (customer.getIsVip() && newLimit.compareTo(vipMinimumLimit) < 0) {
            throw new VipMinimumLimitException(vipMinimumLimit);
        }
    }

    private void saveCreditLimitHistory(Customer customer, User loggedUser,
                                        BigDecimal previousLimit, BigDecimal newLimit) {
        CreditLimitHistory history = CreditLimitHistory.builder()
                .customer(customer)
                .changedBy(loggedUser)
                .previousLimit(previousLimit)
                .newLimit(newLimit)
                .changedAt(LocalDateTime.now())
                .build();

        creditLimitHistoryRepository.save(history);
    }
}