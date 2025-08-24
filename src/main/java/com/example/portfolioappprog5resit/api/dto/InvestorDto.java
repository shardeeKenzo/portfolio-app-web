package com.example.portfolioappprog5resit.api.dto;

import com.example.portfolioappprog5resit.domain.Investor;

import java.time.LocalDate;

public record InvestorDto(
        int id,
        String name,
        String contactDetails,
        LocalDate birthDate,
        String riskProfile
) {
    public static InvestorDto from(Investor i) {
        return new InvestorDto(i.getId(),
                i.getName(),
                i.getContactDetails(),
                i.getBirthDate(),
                i.getRiskProfile());
    }
}
