package com.example.portfolioappprog5resit.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "brokerage_accounts",
        indexes = {
                @Index(name = "idx_ba_account_number", columnList = "accountNumber"),
                @Index(name = "idx_ba_investor_id", columnList = "investor_id")
        })
public class BrokerageAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Changed to Integer

    @NotBlank(message = "Account number is required")
    @Column(nullable = false, length = 100)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Account type is required")
    @Column(nullable = false, length = 40)
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id")
    private Investor investor;

    @Min(value = 0, message = "Balance cannot be negative")
    @Column(nullable = false)
    private double balance;

    private LocalDate creationDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "account_stocks",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "stock_id")
    )
    private List<Stock> stocks = new ArrayList<>();


    public BrokerageAccount() {

    }

    public BrokerageAccount(Integer id, String accountNumber,
                            double balance, LocalDate creationDate, AccountType accountType) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.creationDate = creationDate;
    }

    // Getters / setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    public Investor getInvestor() { return investor; }
    public void setInvestor(Investor investor) { this.investor = investor; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
    public List<Stock> getStocks() { return stocks; }
    public void setStocks(List<Stock> stocks) { this.stocks = stocks; }

    // Behavior

    /** Keep both sides in sync if Stock has a mappedBy collection. */
    public void addStock(Stock stock) {
        if (stock == null) return;
        if (!stocks.contains(stock)) {
            stocks.add(stock);
            if (stock.getBrokerageAccounts() != null &&
                    !stock.getBrokerageAccounts().contains(this)) {
                stock.getBrokerageAccounts().add(this);
            }
        }
    }

    /** Removes a stock and updates the inverse side if bidirectional. */
    public void removeStock(Stock stock) {
        if (stock == null) return;
        stocks.remove(stock);
        if (stock.getBrokerageAccounts() != null) {
            stock.getBrokerageAccounts().remove(this);
        }
    }


    @Override
    public String toString() {
        return "BrokerageAccount{id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", balance=" + balance +
                ", creationDate=" + creationDate +
                ", accountType=" + accountType + '}';
    }

    /** JPA-safe equals/hashCode: id only (no collections). */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrokerageAccount that)) return false;
        return id != null && Objects.equals(id, that.id);
    }
    @Override
    public int hashCode() { return id == null ? 0 : id.hashCode(); }
}
