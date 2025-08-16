package com.example.portfolioappprog5resit.presentation.viewmodel;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class InvestorForm {

    private int id; // for updates; 0 if new

    @NotBlank(message = "{investorform.name.notblank}")
    @Size(min=2, message="{investorform.name.size}")
    private String name;

    @NotBlank(message = "{investorform.contactdetails.notblank}")
    @Email(message = "{investorform.contactdetails.email}")
    private String contactDetails;

    @Past(message = "{investorform.birthdate.past}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "{investorform.riskprofile.notblank}")
    private String riskProfile;

    public int getId() {
        return id;
    }

    public String getName() { return name; }

    public String getContactDetails() { return contactDetails; }

    public LocalDate getBirthDate() { return birthDate; }

    public String getRiskProfile() { return riskProfile; }


}
