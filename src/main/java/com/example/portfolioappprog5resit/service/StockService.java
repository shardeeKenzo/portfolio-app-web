package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.Stock;

import java.util.List;

public interface StockService {
    void addStock(Stock stock);
    List<Stock> getAllStocks();
    List<Stock> getStocksByCriteria(String symbolInput, String minPriceInput, String maxPriceInput);
    Stock findById(int id);
    void deleteById(int id);
    List<Stock> findByIds(List<Integer> ids);
    List<Stock> findAllNotInAccount(int accountId);
}
