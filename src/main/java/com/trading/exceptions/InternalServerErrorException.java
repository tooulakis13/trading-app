package com.trading.exceptions;

public class InternalServerErrorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InternalServerErrorException(String errorMessage) {
        super("Internal Server Error - " + errorMessage);
    }

    public InternalServerErrorException(String errorMessage, Throwable e) {
        super("Internal Server Error - " + errorMessage, e);
    }
}
