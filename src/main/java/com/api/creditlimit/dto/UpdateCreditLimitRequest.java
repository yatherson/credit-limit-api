package com.api.creditlimit.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateCreditLimitRequest(

        @NotNull(message = "New limit cannot be null")
        @DecimalMin(value = "0.0", message = "Limit cannot be negative")
        BigDecimal newLimit
) {
 }