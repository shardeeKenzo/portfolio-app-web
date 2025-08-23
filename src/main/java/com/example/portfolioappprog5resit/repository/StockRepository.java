package com.example.portfolioappprog5resit.repository;

import com.example.portfolioappprog5resit.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

    // ----- Derived queries used by the default bridge methods -----
    List<Stock> findBySymbolContainingIgnoreCase(String symbol);
    List<Stock> findByCurrentPriceBetween(double min, double max);
    List<Stock> findByCurrentPriceGreaterThanEqual(double min);
    List<Stock> findByCurrentPriceLessThanEqual(double max);
    List<Stock> findBySymbolContainingIgnoreCaseAndCurrentPriceBetween(String symbol, double min, double max);
    List<Stock> findByIdIn(List<Integer> ids);

    @Modifying
    @Query(value = "DELETE FROM account_stocks WHERE stock_id = :stockId", nativeQuery = true)
    void deleteLinksForStock(@Param("stockId") int stockId);

    @Query("""
           select s from Stock s
           where not exists (
               select 1 from BrokerageAccount ba
               join ba.stocks bs
               where ba.id = :accountId and bs.id = s.id
           )
           """)
    List<Stock> findAllNotInAccount(@Param("accountId") int accountId);

    boolean existsByIdAndCreatedBy_Id(int id, Integer userId);

    @Query("select s.createdBy.id from Stock s where s.id = :id")
    Integer findOwnerId(@Param("id") int id);

    @Query("select s from Stock s left join fetch s.createdBy where s.id = :id")
    Optional<Stock> findByIdWithCreator(@Param("id") int id);

    // Bridge method to keep old API: int -> Optional<Stock> -> Stock|null
    default Stock findById(int id) {
        return ((JpaRepository<Stock, Integer>) this)
                .findById(Integer.valueOf(id))
                .orElse(null);
    }

    default List<Stock> findByCriteria(String symbolInput, String minPriceInput, String maxPriceInput) {
        String sym = (symbolInput == null || symbolInput.isBlank()) ? null : symbolInput.trim();
        Double min = null, max = null;
        try { if (minPriceInput != null && !minPriceInput.isBlank()) min = Double.parseDouble(minPriceInput.trim()); } catch (Exception ignore) {}
        try { if (maxPriceInput != null && !maxPriceInput.isBlank()) max = Double.parseDouble(maxPriceInput.trim()); } catch (Exception ignore) {}

        if (sym != null && min != null && max != null) {
            return findBySymbolContainingIgnoreCaseAndCurrentPriceBetween(sym, min, max);
        }
        if (sym != null && min != null) return intersect(findBySymbolContainingIgnoreCase(sym), findByCurrentPriceGreaterThanEqual(min));
        if (sym != null && max != null) return intersect(findBySymbolContainingIgnoreCase(sym), findByCurrentPriceLessThanEqual(max));
        if (min != null && max != null) return findByCurrentPriceBetween(min, max);
        if (sym != null) return findBySymbolContainingIgnoreCase(sym);
        if (min != null) return findByCurrentPriceGreaterThanEqual(min);
        if (max != null) return findByCurrentPriceLessThanEqual(max);
        return findAll();
    }

    @Query("select s from BrokerageAccount ba join ba.stocks s where ba.id = :accountId")
    List<Stock> findAllByAccountId(@Param("accountId") int accountId);

    default List<Stock> findAllStocksByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return findByIdIn(ids);
    }

    // small helper: preserve order & uniqueness pragmatically
    private static <T> List<T> intersect(List<T> a, List<T> b) {
        if (a == null || b == null) return List.of();
        List<T> out = new ArrayList<>();
        for (T t : a) if (b.contains(t)) out.add(t);
        return out;
    }
}
