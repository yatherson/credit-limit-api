package com.api.creditlimit.controller;

import com.api.creditlimit.domain.AppUser;
import com.api.creditlimit.dto.CreditLimitHistoryResponse;
import com.api.creditlimit.dto.CreditLimitResponse;
import com.api.creditlimit.dto.UpdateCreditLimitRequest;
import com.api.creditlimit.dto.UpdateCreditLimitResponse;
import com.api.creditlimit.service.CreditLimitService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CreditLimitController {

    private final CreditLimitService creditLimitService;

    public CreditLimitController(CreditLimitService creditLimitService) {
        this.creditLimitService = creditLimitService;
    }

    @GetMapping("/{id}/credit-limit")
    @ResponseStatus(HttpStatus.OK)
    public CreditLimitResponse getCreditLimit(@PathVariable Long id) {
        return creditLimitService.getCreditLimit(id);
    }

    @PutMapping("/{id}/credit-limit")
    @ResponseStatus(HttpStatus.OK)
    public UpdateCreditLimitResponse updateCreditLimit(
            @PathVariable Long id,
            @RequestBody @Valid UpdateCreditLimitRequest request,
            @AuthenticationPrincipal AppUser loggedUser) {
        return creditLimitService.updateCreditLimit(id, request, loggedUser);
    }

    @GetMapping("/{id}/credit-limit/history")
    @ResponseStatus(HttpStatus.OK)
    public Page<CreditLimitHistoryResponse> getCreditLimitHistory(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "changedAt") Pageable pageable) {
        return creditLimitService.getCreditLimitHistory(id, pageable);
    }
}