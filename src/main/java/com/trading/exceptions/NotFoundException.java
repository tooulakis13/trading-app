package com.trading.exceptions;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException(String errorMessage) {
        super("Not found - " + errorMessage);
    }

    public NotFoundException(String errorMessage, Throwable e) {
        super("Not found - " + errorMessage, e);
    }
}
