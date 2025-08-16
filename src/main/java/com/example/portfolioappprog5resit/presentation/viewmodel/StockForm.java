package com.example.portfolioappprog5resit.presentation.viewmodel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class StockForm {

    private int id; // 0 if new

    @NotBlank(message = "{stockform.symbol.notblank}")
    private String symbol;

    @NotBlank(message = "{stockform.companyname.notblank}")
    private String companyName;

    @Min(value=0, message="{stockform.currentprice.min}")
    private double currentPrice;

    @NotBlank(message = "{stockform.sector.notblank}")
    private String sector;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate listedDate;

    private String imageURL;
}
