package com.example.portfolioappprog5resit.api.dto;

import com.example.portfolioappprog5resit.domain.Sector;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/** Used for POST /api/stocks */
public record NewStockDto(
        @NotBlank @Size(min = 1, max = 50) String symbol,
        @NotBlank @Size(min = 3, max = 200) String companyName,
        @PositiveOrZero double currentPrice,
        @NotNull Sector sector,
        @PastOrPresent LocalDate listedDate,
        @Size(max = 500)
        @Pattern(
                regexp = "^$|https?://\\S+$",
                message = "imageURL must be empty or a valid http(s) URL"
        ) String imageURL
) {}
