package com.trading.exceptions;

public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BadRequestException(String errorMessage) {
        super("Bad request - " + errorMessage);
    }

    public BadRequestException(String errorMessage, Throwable e) {
        super("Bad request - " + errorMessage, e);
    }
}
