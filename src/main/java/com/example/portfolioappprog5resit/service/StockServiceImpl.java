package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional
    public void addStock(Stock stock) {
        try {
            stockRepository.save(stock);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to add stock", e);
        }
    }

    @Override
    public List<Stock> getAllStocks() {
        try {
            return stockRepository.findAll();
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to retrieve stocks", e);
        }
    }

    @Override
    public List<Stock> getStocksByCriteria(String symbolInput, String minPriceInput, String maxPriceInput) {
        try {
            return stockRepository.findByCriteria(symbolInput, minPriceInput, maxPriceInput);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to filter stocks by criteria", e);
        }
    }

    @Override
    public Stock findById(int id) {
        try {
            return stockRepository.findById(id);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to retrieve stock details", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        try {
            stockRepository.deleteById(id);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to delete stock", e);
        }
    }

    @Override
    public List<Stock> findByIds(List<Integer> ids) {
        try {
            return stockRepository.findAllStocksByIds(ids);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to retrieve stocks by IDs", e);
        }
    }
}
