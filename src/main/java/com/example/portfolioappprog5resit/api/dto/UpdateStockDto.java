package com.example.portfolioappprog5resit.api.dto;

import com.example.portfolioappprog5resit.domain.Sector;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

public record UpdateStockDto(
        @Size(min = 1, max = 50) String symbol,
        @Size(min = 3, max = 200) String companyName,
        @PositiveOrZero Double currentPrice,
        Sector sector,
        @PastOrPresent LocalDate listedDate,
        @Size(max = 500)
        @Pattern(
                regexp = "^$|https?://\\S+$",
                message = "imageURL must be empty or a valid http(s) URL"
        )   String imageURL
) {}
