package com.example.portfolioappprog5resit.MVCtests;

import com.example.portfolioappprog5resit.config.security.CustomUserDetails;
import com.example.portfolioappprog5resit.domain.AppUser;
import com.example.portfolioappprog5resit.domain.Sector;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.repository.AppUserRepository;
import com.example.portfolioappprog5resit.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full MVC (Thymeleaf) integration tests for StockController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StockControllerMvcIT {

    @Autowired private MockMvc mockMvc;
    @Autowired private StockRepository stockRepository;
    @Autowired private AppUserRepository appUserRepository;

    private AppUser admin;
    private AppUser owner;
    private AppUser stranger;
    private Stock ownersStock;

    private CustomUserDetails as(AppUser u) {
        return new CustomUserDetails(u, List.of(new SimpleGrantedAuthority(u.getRole())));
    }

    @BeforeEach
    void init() {
        stockRepository.deleteAll();
        appUserRepository.deleteAll();

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

        Stock st = new Stock(0, "ACME", "Acme Corp", 123.45, Sector.INDUSTRIAL, LocalDate.of(2024, 1, 1));
        st.setCreatedBy(owner);
        ownersStock = stockRepository.save(st);
    }

    // ---------- Views with model & params ----------

    @Test
    void showStocksView_returnsStocksViewWithModel() throws Exception {
        mockMvc.perform(get("/stocks"))
                .andExpect(status().isOk())
                .andExpect(view().name("stocks"))
                .andExpect(model().attributeExists("stocks"));
    }

    @Test
    void filterStocks_withQueryParams_returnsFilteredModel() throws Exception {
        mockMvc.perform(get("/stocks/filter")
                        .param("symbol", "ACM")
                        .param("minPriceInput", "100")
                        .param("maxPriceInput", "200"))
                .andExpect(status().isOk())
                .andExpect(view().name("stocks"))
                .andExpect(model().attributeExists("stocks"));
    }

    @Test
    void showStockDetails_pathVariableId_returnsStockdetailsView() throws Exception {
        mockMvc.perform(get("/stocks/{id}", ownersStock.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("stockdetails"))
                .andExpect(model().attributeExists("stock"));
    }

    // ---------- MVC creation with @AuthenticationPrincipal ----------

    @Test
    void addStock_withPrincipal_redirectsAndPersists() throws Exception {
        mockMvc.perform(post("/stocks/addstock")
                        .with(user(as(owner))).with(csrf())
                        .param("symbol", "MSFT")
                        .param("companyName", "Microsoft")
                        .param("currentPrice", "390.00")
                        .param("sector", "TECHNOLOGIES")
                        .param("listedDate", "2024-06-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stocks"));

        Stock msft = stockRepository.findAll().stream()
                .filter(s -> "MSFT".equals(s.getSymbol()))
                .findFirst()
                .orElse(null);
        assertThat(msft).isNotNull();
        assertThat(msft.getCreatedBy().getId()).isEqualTo(owner.getId());
    }

    // ---------- MVC delete with @AuthenticationPrincipal (authorization rule) ----------

    @Test
    void deleteStock_asOwner_redirects() throws Exception {
        mockMvc.perform(post("/stocks/delete/{id}", ownersStock.getId())
                        .with(user(as(owner))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stocks"));

        Stock gone = stockRepository.findById(ownersStock.getId());
        assertThat(gone).isNull();
    }

    @Test
    void deleteStock_asAdmin_redirects() throws Exception {
        Stock another = new Stock(0, "GOOG", "Alphabet", 130.0, Sector.TECHNOLOGIES, LocalDate.of(2024, 1, 2));
        another.setCreatedBy(owner);
        another = stockRepository.save(another);

        mockMvc.perform(post("/stocks/delete/{id}", another.getId())
                        .with(user(as(admin))).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/stocks"));
    }

    @Test
    void deleteStock_asStranger_resultsInAccessDeniedFromService() throws Exception {
        mockMvc.perform(post("/stocks/delete/{id}", ownersStock.getId())
                        .with(user(as(stranger))).with(csrf()))
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(AccessDeniedException.class));
        // Note: Your MVC controller doesn't handle AccessDeniedException explicitly.
        // If you add a @ControllerAdvice to map it to 403, change assertion to status().isForbidden().
    }
}
