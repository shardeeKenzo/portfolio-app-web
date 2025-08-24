package com.example.portfolioappprog5resit.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record InvestorCreateDto(
        @NotBlank @Size(min=2, max=200) String name,
        @Email String contactDetails,       // optional
        @NotNull LocalDate birthDate,
        @Size(max=50) String riskProfile    // optional
) {}
