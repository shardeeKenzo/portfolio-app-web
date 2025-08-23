package com.example.portfolioappprog5resit.serviceTests;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.BrokerageAccountRepository;
import com.example.portfolioappprog5resit.repository.StockRepository;
import com.example.portfolioappprog5resit.service.BrokerageAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerageAccountServiceImplTest {

    @Mock
    private BrokerageAccountRepository accountRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private BrokerageAccountServiceImpl service;

    private BrokerageAccount account;
    private Stock s1;
    private Stock s2;

    @BeforeEach
    void init() {
        account = Mockito.spy(new BrokerageAccount()); // spy to verify add/remove calls
        account.setId(10);

        s1 = new Stock();
        s1.setId(1);
        s2 = new Stock();
        s2.setId(2);
    }

    @Test
    void addStocksToAccount_success_addsAllStocks_andDoesNotThrow() {
        int accountId = 10;
        List<Integer> ids = Arrays.asList(1, 2);

        when(accountRepository.findByIdWithStocks(accountId)).thenReturn(account);
        when(stockRepository.findAllById(ids)).thenReturn(List.of(s1, s2));

        service.addStocksToAccount(accountId, ids);

        verify(accountRepository, times(1)).findByIdWithStocks(accountId);
        verify(stockRepository, times(1)).findAllById(ids);
        verify(account, times(1)).addStock(s1);
        verify(account, times(1)).addStock(s2);
// Be precise about repo interactions only; don’t assert “no more” on the spy.
        verifyNoMoreInteractions(stockRepository, accountRepository);
    }

    @Test
    void addStocksToAccount_accountNotFound_throwsDomainException() {
        when(accountRepository.findByIdWithStocks(999)).thenReturn(null);

        assertThatThrownBy(() -> service.addStocksToAccount(999, List.of(1, 2)))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Account not found with ID: 999");

        verify(accountRepository, times(1)).findByIdWithStocks(999);
        verifyNoInteractions(stockRepository);
    }

    @Test
    void removeStockFromAccount_success_removesExactlyOnce() {
        int accountId = 10;
        int stockId = 1;
        when(accountRepository.findByIdWithStocks(accountId)).thenReturn(account);
        when(stockRepository.findById(stockId)).thenReturn(s1);

        service.removeStockFromAccount(accountId, stockId);

        verify(accountRepository, times(1)).findByIdWithStocks(accountId);
        verify(stockRepository, times(1)).findById(stockId);
        verify(account, times(1)).removeStock(s1);
    }

    @Test
    void removeStockFromAccount_accountNotFound_throws() {
        when(accountRepository.findByIdWithStocks(888)).thenReturn(null);

        assertThatThrownBy(() -> service.removeStockFromAccount(888, 1))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Account not found");

        verify(accountRepository, times(1)).findByIdWithStocks(888);
        verifyNoInteractions(stockRepository);
    }

    @Test
    void removeStockFromAccount_stockNotFound_throws() {
        when(accountRepository.findByIdWithStocks(10)).thenReturn(account);
        when(stockRepository.findById(123)).thenReturn(null);

        assertThatThrownBy(() -> service.removeStockFromAccount(10, 123))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Stock not found with ID: 123");

        verify(accountRepository, times(1)).findByIdWithStocks(10);
        verify(stockRepository, times(1)).findById(123);
        verify(account, never()).removeStock(any());
    }

    @Test
    void deleteById_success_callsRepositoryDelete() {
        int id = 55;
        when(accountRepository.findByIdWithStocks(id)).thenReturn(account);

        service.deleteById(id);

        verify(accountRepository, times(1)).findByIdWithStocks(id);
        verify(accountRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteById_notFound_throws_andDoesNotDelete() {
        int id = 777;
        when(accountRepository.findByIdWithStocks(id)).thenReturn(null);

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Failed to delete account with ID: " + id) // outer message
                .cause()
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Account not found with ID: " + id);    // inner cause

        verify(accountRepository, times(1)).findByIdWithStocks(id);
        verify(accountRepository, never()).deleteById(anyInt());
    }
}
