package com.example.portfolioappprog5resit.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PortfolioApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handlePortfolioApplicationException(PortfolioApplicationException ex, Model model) {
        logger.error("PortfolioApplicationException occurred: {}", ex.getMessage(), ex);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("exception", ex);
        return "database-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneralException(Exception ex, Model model) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("exception", ex);
        return "generic-error";
    }

    @ExceptionHandler(InvestorNotFoundException.class)
    public String handleInvestorNotFoundException(InvestorNotFoundException ex, Model model) {
        logger.error("InvestorNotFoundException: {}", ex.getMessage(), ex);
        model.addAttribute("errorMessage", ex.getMessage());
        return "generic-error";
    }
}
