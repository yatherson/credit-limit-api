package com.api.creditlimit.dto;

import com.api.creditlimit.domain.Customer;

import java.math.BigDecimal;

public record CreditLimitResponse(
        Long customerId,
        String customerName,
        Boolean isVip,
        BigDecimal creditLimit
) {
    public static CreditLimitResponse from(Customer customer) {
        return new CreditLimitResponse(
                customer.getId(),
                customer.getName(),
                customer.getIsVip(),
                customer.getCreditLimit()
        );
    }
}