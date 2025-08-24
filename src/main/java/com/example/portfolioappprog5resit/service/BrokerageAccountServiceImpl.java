package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.BrokerageAccountRepository;
import com.example.portfolioappprog5resit.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BrokerageAccountServiceImpl implements BrokerageAccountService {

    private final BrokerageAccountRepository accountRepository;
    private final StockRepository stockRepository;

    public BrokerageAccountServiceImpl(BrokerageAccountRepository accountRepository, StockRepository stockRepository) {
        this.accountRepository = accountRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    public BrokerageAccount findById(int id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public BrokerageAccount findByIdWithStocks(int id) {
        return accountRepository.findByIdWithStocks(id);
    }

    @Override
    @Transactional
    public void addStocksToAccount(int accountId, List<Integer> stockIds) {
        BrokerageAccount account = accountRepository.findByIdWithStocks(accountId);
        if (account == null) throw new PortfolioApplicationException("Account not found with ID: " + accountId);

        List<Stock> stocksToAdd = stockRepository.findAllById(stockIds);
        stocksToAdd.forEach(account::addStock);
    }

    @Override
    @Transactional
    public void removeStockFromAccount(int accountId, int stockId) {
        BrokerageAccount account = accountRepository.findByIdWithStocks(accountId);
        if (account == null) throw new PortfolioApplicationException("Account not found with ID: " + accountId);

        Stock stock = stockRepository.findById(stockId);
        if (stock == null) throw new PortfolioApplicationException("Stock not found with ID: " + stockId);

        account.removeStock(stock);
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
        deleteById(id);
    }

    @Override
    public List<BrokerageAccount> findAll() {
        return accountRepository.findAll();
    }
}
