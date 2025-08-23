package com.example.portfolioappprog5resit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "stocks",
        indexes = {
                @Index(name = "idx_stock_symbol", columnList = "symbol")
        }
)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String symbol;

    @Column(nullable = false, length = 200)
    private String companyName;

    @Column(nullable = false)
    private double currentPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Sector sector;

    private LocalDate listedDate;

    @Column(name = "image_url", length = 500)
    private String imageURL;

    @ManyToMany(mappedBy = "stocks", fetch = FetchType.LAZY)
    private Set<BrokerageAccount> brokerageAccounts = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private AppUser createdBy;


    // Constructors
    public Stock() { }

    public Stock(int id, String symbol, String company_name, double current_price,
                 Sector sector, LocalDate listed_date) {
        this.id = id;
        this.symbol = symbol;
        this.companyName = company_name;
        this.currentPrice = current_price;
        this.sector = sector;
        this.listedDate = listed_date;
    }

    public Stock(int id, String symbol, String company_name, double current_price,
                 Sector sector, LocalDate listed_date, String imageURL) {
        this(id, symbol, company_name, current_price, sector, listed_date);
        this.imageURL = imageURL;
    }

    public Set<BrokerageAccount> getBrokerageAccounts() { return brokerageAccounts; }
    public void setBrokerageAccounts(Set<BrokerageAccount> brokerageAccounts) { this.brokerageAccounts = brokerageAccounts; }

    public int getId() { return id; }
    public String getSymbol() { return symbol; }
    public String getCompanyName() { return companyName; }
    public double getCurrentPrice() { return currentPrice; }
    public Sector getSector() { return sector; }
    public LocalDate getListedDate(){ return listedDate; }
    public String getImageURL() { return imageURL; }

    public void setId(int id) { this.id = id; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }
    public void setSector(Sector sector) { this.sector = sector; }
    public void setListedDate(LocalDate listedDate) { this.listedDate = listedDate; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }

    public AppUser getCreatedBy() { return createdBy; }
    public void setCreatedBy(AppUser createdBy) { this.createdBy = createdBy; }

    @Override
    public String toString() {
        return "Stock{id=" + id + ", symbol='" + symbol + '\'' + ", companyName='" + companyName + '\'' +
                ", currentPrice=" + currentPrice + ", listedDate=" + listedDate + '}';
    }

    /** JPA-safe equality: id-based, no collections. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock other)) return false;
        // transient entities (id==0) are never equal
        return id != 0 && id == other.id;
    }
    @Override
    public int hashCode() { return Integer.hashCode(id); }
}
