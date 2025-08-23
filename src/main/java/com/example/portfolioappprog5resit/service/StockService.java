package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.config.security.CustomUserDetails;
import com.example.portfolioappprog5resit.domain.Stock;

import java.util.List;

public interface StockService {
    void addStock(Stock stock, int creatorUserId);
    List<Stock> getAllStocks();
    List<Stock> getStocksByCriteria(String symbolInput, String minPriceInput, String maxPriceInput);
    Stock findById(int id);
    void deleteByIdAuthorized(int id, CustomUserDetails current);
    List<Stock> findByIds(List<Integer> ids);
    List<Stock> findAllByAccountId(int accountId);
    List<Stock> findAllNotInAccount(int accountId);
    void updateAuthorized(Stock stock, CustomUserDetails current);
    Stock findByIdWithCreator(int id);
}
