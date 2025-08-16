package com.example.portfolioappprog5resit.exception;

public class InvestorNotFoundException extends PortfolioApplicationException {
    public InvestorNotFoundException(String message) {
        super(message);
    }

    public InvestorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
