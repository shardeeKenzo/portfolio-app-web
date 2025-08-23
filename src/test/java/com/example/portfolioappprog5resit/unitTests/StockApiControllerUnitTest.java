package com.example.portfolioappprog5resit.unitTests;

import com.example.portfolioappprog5resit.api.StockApiController;
import com.example.portfolioappprog5resit.api.mapping.StockMapper;
import com.example.portfolioappprog5resit.config.security.CustomUserDetails;
import com.example.portfolioappprog5resit.domain.AppUser;
import com.example.portfolioappprog5resit.domain.Sector;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pure controller-unit tests: mock the service & mapper.
 */
@WebMvcTest(controllers = StockApiController.class)
class StockApiControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @MockitoBean
    private StockMapper stockMapper; // not used by DELETE, but controller needs it in ctor

    private CustomUserDetails asUser(AppUser u) {
        return new CustomUserDetails(u, List.of(new SimpleGrantedAuthority(u.getRole())));
    }

    private AppUser ownerUser;
    private Stock existingStock;

    @BeforeEach
    void setUp() {
        AppUser o = new AppUser();
        o.setId(101);
        o.setUsername("owner");
        o.setPasswordHash("{noop}x");
        o.setRole("ROLE_USER");
        ownerUser = o;

        Stock s = new Stock(1, "ACME", "Acme", 123.45, Sector.INDUSTRIAL, LocalDate.of(2024,1,1));
        existingStock = s;
    }

    @Test
    void delete_unknownId_returns404_andDoesNotCallDelete() throws Exception {
        int unknownId = 9999;
        when(stockService.findById(unknownId)).thenReturn(null);

        mockMvc.perform(delete("/api/stocks/{id}", unknownId)
                        .with(user(asUser(ownerUser))).with(csrf()))
                .andExpect(status().isNotFound());

        verify(stockService, times(1)).findById(unknownId);
        verify(stockService, never()).deleteByIdAuthorized(anyInt(), any());
        verifyNoInteractions(stockMapper);
    }

    @Test
    void delete_asStranger_serviceThrowsAccessDenied_returns403() throws Exception {
        int id = existingStock.getId();
        when(stockService.findById(id)).thenReturn(existingStock);
        doThrow(new org.springframework.security.access.AccessDeniedException("nope"))
                .when(stockService).deleteByIdAuthorized(eq(id), any());

        mockMvc.perform(delete("/api/stocks/{id}", id)
                        .with(user(asUser(ownerUser))).with(csrf()))
                .andExpect(status().isForbidden());

        verify(stockService, times(1)).findById(id);
        verify(stockService, times(1)).deleteByIdAuthorized(eq(id), any());
    }

    @Test
    void delete_asOwner_success_returns204_andInvokesService() throws Exception {
        int id = existingStock.getId();
        when(stockService.findById(id)).thenReturn(existingStock);
        // default: do nothing -> success

        mockMvc.perform(delete("/api/stocks/{id}", id)
                        .with(user(asUser(ownerUser))).with(csrf()))
                .andExpect(status().isNoContent());

        verify(stockService, times(1)).findById(id);
        verify(stockService, times(1)).deleteByIdAuthorized(eq(id), any());
        verifyNoMoreInteractions(stockService);
    }
}
