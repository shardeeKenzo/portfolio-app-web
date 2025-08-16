package com.example.portfolioappprog5resit.api.dto;

import com.example.portfolioappprog5resit.domain.Sector;

import java.time.LocalDate;

public record StockDto(
        int id,
        String symbol,
        String companyName,
        double currentPrice,
        Sector sector,
        LocalDate listedDate,
        String imageURL
) {}
