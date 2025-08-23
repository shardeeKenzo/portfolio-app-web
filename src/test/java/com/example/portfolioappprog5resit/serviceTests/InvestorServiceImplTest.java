package com.example.portfolioappprog5resit.serviceTests;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Investor;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.InvestorRepository;
import com.example.portfolioappprog5resit.service.InvestorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestorServiceImplTest {

    @Mock
    private InvestorRepository investorRepository;

    @InjectMocks
    private InvestorServiceImpl service;

    @Test
    void addAccount_linksBothWays_andSavesInvestor() {
        Investor investor = new Investor(0, "Alice", "c", LocalDate.of(1990,1,1), "Balanced");
        BrokerageAccount account = new BrokerageAccount();
        account.setId(42);

        service.addAccount(investor, account);

        ArgumentCaptor<Investor> captor = ArgumentCaptor.forClass(Investor.class);
        verify(investorRepository, times(1)).save(captor.capture());
        Investor saved = captor.getValue();

        // verify relationship integrity
        assertThat(saved.getAccounts()).contains(account);
        assertThat(account.getInvestor()).isEqualTo(investor);
    }

    @Test
    void addAccount_repoThrows_isWrappedInDomainException() {
        Investor investor = new Investor(0, "Bob", "c", LocalDate.of(1985,2,2), "Aggressive");
        BrokerageAccount account = new BrokerageAccount();
        doThrow(new RuntimeException("db down")).when(investorRepository).save(any());

        assertThatThrownBy(() -> service.addAccount(investor, account))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Failed to add brokerage account");
    }

    @Test
    void getInvestorsByCriteria_passThrough_success() {
        when(investorRepository.findByCriteria("Ali", "1990-01-01"))
                .thenReturn(List.of(new Investor(1, "Alice", null, LocalDate.of(1990,1,1), "Balanced")));

        List<Investor> result = service.getInvestorsByCriteria("Ali", "1990-01-01");

        assertThat(result).hasSize(1);
        verify(investorRepository, times(1)).findByCriteria("Ali", "1990-01-01");
    }

    @Test
    void getInvestorsByCriteria_repoThrows_isWrapped() {
        when(investorRepository.findByCriteria(any(), any()))
                .thenThrow(new RuntimeException("db"));

        assertThatThrownBy(() -> service.getInvestorsByCriteria("x", "y"))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Failed to filter investors");
    }

    @Test
    void addInvestor_success_callsSave() {
        Investor investor = new Investor(0, "Carol", null, LocalDate.of(1995,5,5), "Conservative");
        service.addInvestor(investor);
        verify(investorRepository, times(1)).save(investor);
    }

    @Test
    void addInvestor_repoThrows_isWrapped() {
        Investor investor = new Investor(0, "Dave", null, LocalDate.of(1994,4,4), "Balanced");
        doThrow(new RuntimeException("db")).when(investorRepository).save(investor);

        assertThatThrownBy(() -> service.addInvestor(investor))
                .isInstanceOf(PortfolioApplicationException.class)
                .hasMessageContaining("Failed to add investor");
    }

    @Test
    void findWithAccounts_success_optionalPresent() {
        Investor inv = new Investor(10, "Eva", null, LocalDate.of(1992,3,3), "Aggressive");
        when(investorRepository.findWithAccounts(10)).thenReturn(Optional.of(inv));

        Investor out = service.findWithAccounts(10);

        assertThat(out).isSameAs(inv);
        verify(investorRepository, times(1)).findWithAccounts(10);
    }

    @Test
    void findWithAccounts_empty_returnsNull() {
        when(investorRepository.findWithAccounts(99)).thenReturn(Optional.empty());

        Investor out = service.findWithAccounts(99);

        assertThat(out).isNull();
    }
}
