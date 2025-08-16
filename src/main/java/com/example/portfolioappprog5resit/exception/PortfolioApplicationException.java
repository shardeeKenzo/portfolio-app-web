package com.example.portfolioappprog5resit.exception;

public class PortfolioApplicationException extends RuntimeException {
    public PortfolioApplicationException(String message) {
        super(message);
    }

    public PortfolioApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
