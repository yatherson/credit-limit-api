package com.api.creditlimit.dto;

import com.api.creditlimit.domain.CreditLimitHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditLimitHistoryResponse(
        Long id,
        BigDecimal previousLimit,
        BigDecimal newLimit,
        String changedBy,
        LocalDateTime changedAt
) {
    public static CreditLimitHistoryResponse from(CreditLimitHistory history) {
        return new CreditLimitHistoryResponse(
                history.getId(),
                history.getPreviousLimit(),
                history.getNewLimit(),
                history.getChangedBy().getUsername(),
                history.getChangedAt()
        );
    }
}