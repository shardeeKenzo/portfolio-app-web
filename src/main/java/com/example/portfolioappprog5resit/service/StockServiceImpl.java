package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.config.security.CustomUserDetails;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.AppUserRepository;
import com.example.portfolioappprog5resit.repository.StockRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final AppUserRepository appUserRepository;

    public StockServiceImpl(StockRepository stockRepository, AppUserRepository appUserRepository) {
        this.stockRepository = stockRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<Stock> findAllNotInAccount(int accountId) {
        return stockRepository.findAllNotInAccount(accountId);
    }

    @Override
    @Transactional
    public void addStock(Stock stock, int creatorUserId) {
        var creator = appUserRepository.findById(creatorUserId)
                .orElseThrow(() -> new PortfolioApplicationException("Unknown user"));
        stock.setCreatedBy(creator);               // ← attach creator here
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
    public void deleteByIdAuthorized(int id, CustomUserDetails current) {
        boolean isAdmin = current.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin) {
            boolean isOwner = stockRepository.existsByIdAndCreatedBy_Id(id, current.getUserId());
            if (!isOwner) {
                // mirror Spring Security semantics
                throw new org.springframework.security.access.AccessDeniedException("Not allowed to delete this stock");
            }
        }
        try {
            stockRepository.deleteLinksForStock(id);
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

    @Override
    public List<Stock> findAllByAccountId(int accountId) {
        return stockRepository.findAllByAccountId(accountId);
    }
    @Override
    public Stock findByIdWithCreator(int id) {
        try {
            return stockRepository.findByIdWithCreator(id).orElse(null);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to retrieve stock (with owner)", e);
        }
    }

    @Override
    @Transactional
    public void updateAuthorized(Stock stock,
                                 CustomUserDetails current) {

        if (stock == null || stock.getId() == 0) {
            throw new IllegalArgumentException("Stock must have a valid ID for update");
        }
        if (current == null) {
            // Controller should already require auth for write endpoints, but double-check:
            throw new AccessDeniedException("Authentication required");
        }

        // Admin?
        boolean isAdmin = current.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        // Owner? (check against DB, not the passed object)
        boolean isOwner = stockRepository.existsByIdAndCreatedBy_Id(stock.getId(), current.getUserId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Not allowed to update this stock");
        }

        // Lock creator: never allow changing createdBy via API/mapper
        Integer ownerId = stockRepository.findOwnerId(stock.getId());
        if (ownerId == null) {
            throw new PortfolioApplicationException("Cannot determine stock owner for id=" + stock.getId());
        }
        // Re-attach the original creator reference from the DB
        stock.setCreatedBy(appUserRepository.getReferenceById(ownerId));

        // Persist changes (merge if detached)
        try {
            stockRepository.save(stock);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to update stock", e);
        }
    }
}
