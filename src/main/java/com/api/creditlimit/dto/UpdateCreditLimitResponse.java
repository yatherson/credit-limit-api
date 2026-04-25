package com.api.creditlimit.dto;

import com.api.creditlimit.domain.Customer;
import java.math.BigDecimal;

public record UpdateCreditLimitResponse(
        Long customerId,
        String customerName,
        BigDecimal previousLimit,
        BigDecimal newLimit
) {
    public static UpdateCreditLimitResponse from(Customer customer, BigDecimal previousLimit) {
        return new UpdateCreditLimitResponse(
                customer.getId(),
                customer.getName(),
                previousLimit,
                customer.getCreditLimit()
        );
    }
}