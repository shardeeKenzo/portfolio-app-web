package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.BrokerageAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BrokerageAccountServiceImpl implements BrokerageAccountService {

    private final BrokerageAccountRepository accountRepository;

    public BrokerageAccountServiceImpl(BrokerageAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public BrokerageAccount findById(int id) {
        // If you want stocks too, prefer findByIdWithStocks
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public BrokerageAccount findByIdWithStocks(int id) {
        return accountRepository.findByIdWithStocks(id);
    }

    @Override
    @Transactional
    public void addStocksToAccount(BrokerageAccount account, List<Stock> stocksToAdd) {
        try {
            if (account == null || stocksToAdd == null) return;
            for (Stock stock : stocksToAdd) {
                account.addStock(stock);
            }
            accountRepository.save(account);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to add stocks to account", e);
        }
    }

    @Override
    @Transactional
    public void removeStockFromAccount(BrokerageAccount account, Stock stock) {
        try {
            if (account == null || stock == null) return;
            account.removeStock(stock);
            accountRepository.save(account);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to remove stock from account", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        try {
            BrokerageAccount account = accountRepository.findByIdWithStocks(id);
            if (account == null) {
                throw new PortfolioApplicationException("Account not found with ID: " + id);
            }
            accountRepository.deleteById(id);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to delete account with ID: " + id, e);
        }
    }

    @Override
    @Transactional
    public void deleteAccount(int id) {
        // keep your original “alias” method
        deleteById(id);
    }

    @Override
    public List<BrokerageAccount> findAll() {
        return accountRepository.findAll();
    }
}
