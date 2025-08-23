package com.example.portfolioappprog5resit.repoTests;

import com.example.portfolioappprog5resit.domain.*;
import com.example.portfolioappprog5resit.repository.AppUserRepository;
import com.example.portfolioappprog5resit.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class StockRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser owner;

    @BeforeEach
    void setup() {
        AppUser u = new AppUser();
        u.setUsername("owner");
        u.setPasswordHash("{noop}x");
        u.setRole("ROLE_USER");
        owner = appUserRepository.saveAndFlush(u);
    }

    @Test
    void deleteStock_shouldRemoveJoinTableLinks_butNotOtherEntities() {
        // arrange
        Stock stock = new Stock(0, "TSLA", "Tesla", 200.0, Sector.CAR_MANUFACTURES, LocalDate.now());
        stock.setCreatedBy(owner);
        stockRepository.saveAndFlush(stock);

        Investor investor = new Investor(0, "Elon", null, LocalDate.of(1971, 6, 28), "Aggressive");
        entityManager.persist(investor);

        BrokerageAccount account = new BrokerageAccount(null, "ACC-1", 10000, LocalDate.now(), AccountType.INDIVIDUAL);
        account.setInvestor(investor);
        account.addStock(stock);
        entityManager.persist(account);

        entityManager.flush();
        entityManager.clear();

        // act (mirror your service’s order)
        stockRepository.deleteLinksForStock(stock.getId());
        stockRepository.deleteById(stock.getId());
        entityManager.flush();
        entityManager.clear();

        // assert: stock gone (your bridge findById(int) returns Stock|null)
        Stock deleted = stockRepository.findById(stock.getId());
        assertThat(deleted).isNull();

        // assert: account still exists and join rows are gone
        BrokerageAccount reloaded = entityManager.find(BrokerageAccount.class, account.getId());
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getStocks()).isEmpty();
    }

    @Test
    void uniqueUsername_shouldBeEnforced() {
        AppUser u1 = new AppUser();
        u1.setUsername("dup");
        u1.setPasswordHash("{noop}x");
        u1.setRole("ROLE_USER");
        appUserRepository.saveAndFlush(u1);

        AppUser u2 = new AppUser();
        u2.setUsername("dup"); // same username
        u2.setPasswordHash("{noop}y");
        u2.setRole("ROLE_ADMIN");

        assertThatThrownBy(() -> appUserRepository.saveAndFlush(u2))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    @Test
    void createdBy_isLazy_byDefault_and_Eager_whenUsingFetchQuery() {
        Stock stock = new Stock(0, "NVDA", "NVIDIA", 999.99, Sector.TECHNOLOGIES, LocalDate.now());
        stock.setCreatedBy(owner);
        stockRepository.saveAndFlush(stock);
        entityManager.clear();

        // default bridge findById(int) -> returns entity; createdBy should be LAZY (not loaded)
        Stock s1 = stockRepository.findById(stock.getId());
        PersistenceUnitUtil util = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        assertThat(util.isLoaded(s1, "createdBy"))
                .as("default find should not load createdBy")
                .isFalse();

        // left join fetch -> Optional<Stock>, createdBy should be loaded
        Stock s2 = stockRepository.findByIdWithCreator(stock.getId()).orElseThrow();
        assertThat(util.isLoaded(s2, "createdBy")).isTrue();
    }
}
