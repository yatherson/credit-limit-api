package com.api.creditlimit.exception;

public class VipMinimumLimitException extends RuntimeException {

    public VipMinimumLimitException(java.math.BigDecimal minimumLimit) {
        super("VIP customers must have a minimum credit limit of " + minimumLimit);
    }
}