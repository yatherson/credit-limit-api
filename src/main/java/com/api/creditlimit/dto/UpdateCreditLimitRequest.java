package com.api.creditlimit.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateCreditLimitRequest(

        @NotNull(message = "New limit cannot be null")
        BigDecimal newLimit
) {
 }