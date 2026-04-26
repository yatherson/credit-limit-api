package com.api.creditlimit.exception;

public class NegativeCreditLimitException extends RuntimeException {

    public NegativeCreditLimitException() {
        super("Credit limit cannot be negative");
    }
}