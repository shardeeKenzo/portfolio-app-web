package com.example.portfolioappprog5resit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "investors",
        indexes = {
                @Index(name = "idx_investor_name", columnList = "name"),
                @Index(name = "idx_investor_birthdate", columnList = "birthDate")
        })
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String contactDetails;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 50)
    private String riskProfile;

    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BrokerageAccount> accounts = new ArrayList<>();

    // Constructors
    public Investor() { }

    public Investor(int id_, String name_, String contact_details, LocalDate birth_date, String risk_profile) {
        this.id = id_;
        this.name = name_;
        this.contactDetails = contact_details;
        this.birthDate = birth_date;
        this.riskProfile = risk_profile;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getContactDetails() { return contactDetails; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getRiskProfile() { return riskProfile; }
    public List<BrokerageAccount> getAccounts() { return accounts; }


    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setContactDetails(String contactDetails) { this.contactDetails = contactDetails; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setRiskProfile(String riskProfile) { this.riskProfile = riskProfile; }

    @Override
    public String toString() {
        return "Investor id=" + id + ", name='" + name + '\'' +
                ", birthDate=" + birthDate +
                ", riskProfile='" + riskProfile + '\'' +
                ", accounts=" + (accounts != null ? accounts.size() : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Investor other)) return false;
        if (id == 0 || other.id == 0) return false;
        return id == other.id;
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
