package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Stock;

import java.util.List;

public interface BrokerageAccountService {
    BrokerageAccount findById(int id);
    BrokerageAccount findByIdWithStocks(int id);

    void addStocksToAccount(BrokerageAccount account, List<Stock> stocksToAdd);
    void removeStockFromAccount(BrokerageAccount account, Stock stock);

    void deleteById(int id);
    void deleteAccount(int id); // alias kept for compatibility

    List<BrokerageAccount> findAll();
}
