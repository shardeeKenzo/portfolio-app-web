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
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full-stack tests for the REST API controller.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StockApiControllerIT {

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

    // ---------- GET with query params (200 vs 204) ----------

    @Test
    void getStocks_withMatchingCriteria_returns200AndList() throws Exception {
        mockMvc.perform(get("/api/stocks")
                        .param("symbol", "ACM")
                        .param("minPrice", "100")
                        .param("maxPrice", "200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].symbol").value("ACME"));
    }

    @Test
    void getStocks_withNoMatches_returns204() throws Exception {
        mockMvc.perform(get("/api/stocks")
                        .param("symbol", "ZZZ")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // ---------- POST create (auth required) ----------

    @Test
    void create_asAuthenticatedUser_returns201_withLocation_andBody() throws Exception {
        String json = """
            {
              "symbol":"AMD",
              "companyName":"Advanced Micro Devices",
              "currentPrice":150.0,
              "sector":"TECHNOLOGIES",
              "listedDate":"2024-02-01",
              "imageURL":null
            }
            """;

        mockMvc.perform(post("/api/stocks")
                        .with(user(as(owner))).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesRegex("/api/stocks/\\d+")))
                .andExpect(jsonPath("$.symbol").value("AMD"));

        List<Stock> all = stockRepository.findAll();
        assertThat(all.stream().anyMatch(s -> "AMD".equals(s.getSymbol()))).isTrue();
    }

    @Test
    void create_anonymous_returns403() throws Exception {
        String json = """
            {"symbol":"NVDA","companyName":"NVIDIA","currentPrice":999.99,"sector":"TECHNOLOGIES","listedDate":"2024-03-01"}
            """;

        mockMvc.perform(post("/api/stocks").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    // ---------- PATCH update (authorization: owner or admin) ----------

    @Test
    void patch_asOwner_updatesAndReturns200() throws Exception {
        String json = """
            {"companyName":"Acme International","currentPrice":200.50}
            """;

        mockMvc.perform(patch("/api/stocks/{id}", ownersStock.getId())
                        .with(user(as(owner))).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("Acme International"))
                .andExpect(jsonPath("$.currentPrice").value(200.50));
    }

    @Test
    void patch_asStranger_returns403() throws Exception {
        String json = """
            {"companyName":"Hacked Name"}
            """;

        mockMvc.perform(patch("/api/stocks/{id}", ownersStock.getId())
                        .with(user(as(stranger))).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    void patch_unknownId_returns404() throws Exception {
        String json = """
        {
          "companyName":"DoesNotMatter",
          "currentPrice": 200.00
        }
        """;

        mockMvc.perform(patch("/api/stocks/{id}", 999999)
                        .with(user(as(owner))).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // ---------- DELETE (authorization: owner or admin) ----------

    @Test
    void delete_asOwner_returns204_andRemoves() throws Exception {
        mockMvc.perform(delete("/api/stocks/{id}", ownersStock.getId())
                        .with(user(as(owner))).with(csrf()))
                .andExpect(status().isNoContent());

        Stock shouldBeNull = stockRepository.findById(ownersStock.getId());
        assertThat(shouldBeNull).isNull();
    }

    @Test
    void delete_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/stocks/{id}", ownersStock.getId())
                        .with(user(as(admin))).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_asStranger_returns403() throws Exception {
        mockMvc.perform(delete("/api/stocks/{id}", ownersStock.getId())
                        .with(user(as(stranger))).with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void delete_unknownId_returns404() throws Exception {
        mockMvc.perform(delete("/api/stocks/{id}", 424242)
                        .with(user(as(owner))).with(csrf()))
                .andExpect(status().isNotFound());
    }
}
