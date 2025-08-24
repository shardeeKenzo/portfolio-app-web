package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Stock;

import java.util.List;

public interface BrokerageAccountService {
    BrokerageAccount findById(int id);
    BrokerageAccount findByIdWithStocks(int id);

    void addStocksToAccount(int accountId, List<Integer> stockIds);
    void removeStockFromAccount(int accountId, int stockId);

    void deleteById(int id);
    void deleteAccount(int id);

    List<BrokerageAccount> findAll();
}
