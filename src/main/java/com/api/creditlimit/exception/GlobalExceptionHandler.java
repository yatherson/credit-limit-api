package com.api.creditlimit.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerNotFoundException.class)
    public ProblemDetail handleCustomerNotFoundException(CustomerNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(404), ex.getMessage()
        );
        problem.setType(URI.create("/errors/customer-not-found"));
        problem.setTitle("Customer Not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(NegativeCreditLimitException.class)
    public ProblemDetail handleNegativeCreditLimitException(NegativeCreditLimitException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(422), ex.getMessage()
        );
        problem.setType(URI.create("/errors/negative-credit-limit"));
        problem.setTitle("Negative Credit Limit");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(VipMinimumLimitException.class)
    public ProblemDetail handleVipMinimumLimitException(VipMinimumLimitException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(422), ex.getMessage()
        );
        problem.setType(URI.create("/errors/vip-minimum-limit"));
        problem.setTitle("VIP Minimum Limit Violation");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}