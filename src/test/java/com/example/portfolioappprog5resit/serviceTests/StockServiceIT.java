package com.example.portfolioappprog5resit.serviceTests;

import com.example.portfolioappprog5resit.config.security.CustomUserDetails;
import com.example.portfolioappprog5resit.domain.*;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.AppUserRepository;
import com.example.portfolioappprog5resit.repository.StockRepository;
import com.example.portfolioappprog5resit.service.StockService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StockServiceIT {

    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private AppUser admin;
    private AppUser owner;
    private AppUser stranger;

    @BeforeEach
    void seedUsers() {
        AppUser a = new AppUser();
        a.setUsername("admin");
        a.setPasswordHash("{noop}x");
        a.setRole("ROLE_ADMIN");
        admin = appUserRepository.save(a);

        AppUser o = new AppUser();
        o.setUsername("owner");
        o.setPasswordHash("{noop}x");
        o.setRole("ROLE_USER");
        owner = appUserRepository.save(o);

        AppUser s = new AppUser();
        s.setUsername("stranger");
        s.setPasswordHash("{noop}x");
        s.setRole("ROLE_USER");
        stranger = appUserRepository.save(s);
    }

    private CustomUserDetails cud(AppUser u) {
        return new CustomUserDetails(u, List.of(new SimpleGrantedAuthority(u.getRole())));
    }

    @Test
    void addStock_attachesCreator_andPersists() {
        Stock stock = new Stock(0, "AMD", "Advanced Micro Devices", 150.0, Sector.TECHNOLOGIES, LocalDate.now());
        stockService.addStock(stock, owner.getId());

        assertThat(stock.getId()).isNotZero();
        Stock fromDb = stockRepository.findByIdWithCreator(stock.getId()).orElseThrow();
        assertThat(fromDb.getCreatedBy().getUsername()).isEqualTo("owner");
    }

    @Test
    void addStock_unknownUser_shouldThrowDomainException() {
        Stock stock = new Stock(0, "META", "Meta", 400.0, Sector.TECHNOLOGIES, LocalDate.now());
        assertThatThrownBy(() -> stockService.addStock(stock, -123))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Unknown user");
    }

    @Test
    void deleteByIdAuthorized_owner_canDelete_andLinksAreCleared() {
        // arrange
        Stock stock = new Stock(0, "AAPL", "Apple", 190.0, Sector.TECHNOLOGIES, LocalDate.now());
        stock.setCreatedBy(owner);
        stock = stockRepository.save(stock);

        Investor investor = new Investor(0, "Long Holder", null, LocalDate.of(1990,1,1), "Balanced");
        BrokerageAccount account = new BrokerageAccount(null, "ACC-42", 5000, LocalDate.now(), AccountType.INDIVIDUAL);
        account.setInvestor(investor);
        account.addStock(stock);

        entityManager.persist(investor);
        entityManager.persist(account);
        entityManager.flush();
        entityManager.clear();

        // act
        stockService.deleteByIdAuthorized(stock.getId(), cud(owner));

        // assert: entity gone and join cleared
        Stock shouldBeNull = stockRepository.findById(stock.getId());
        assertThat(shouldBeNull).isNull();

        BrokerageAccount reloaded = entityManager.find(BrokerageAccount.class, account.getId());
        assertThat(reloaded.getStocks()).isEmpty();
    }

    @Test
    void deleteByIdAuthorized_admin_canDelete_anyStock() {
        Stock stock = new Stock(0, "GOOG", "Alphabet", 130.0, Sector.TECHNOLOGIES, LocalDate.now());
        stock.setCreatedBy(owner);
        stock = stockRepository.save(stock);

        stockService.deleteByIdAuthorized(stock.getId(), cud(admin));

        Stock shouldBeNull = stockRepository.findById(stock.getId());
        assertThat(shouldBeNull).isNull();
    }

    @Test
    void deleteByIdAuthorized_nonOwner_nonAdmin_shouldFail() {
        Stock stock = new Stock(0, "MSFT", "Microsoft", 390.0, Sector.TECHNOLOGIES, LocalDate.now());
        stock.setCreatedBy(owner);
        stock = stockRepository.save(stock);

        Stock finalStock = stock;
        assertThatThrownBy(() -> stockService.deleteByIdAuthorized(finalStock.getId(), cud(stranger)))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
