package com.example.portfolioappprog5resit.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "application_user",
        uniqueConstraints = @UniqueConstraint(name="uk_appuser_username", columnNames="username"))
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false, length=120)
    private String username;

    @Column(nullable=false, length=100)
    private String passwordHash;

    @Column(nullable=false, length=20)
    private String role;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private Set<Stock> createdStocks = new HashSet<>();

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getPasswordHash() { return passwordHash; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }


    public Set<Stock> getCreatedStocks() { return createdStocks; }
    public void setCreatedStocks(Set<Stock> s) { this.createdStocks = s; }
}
