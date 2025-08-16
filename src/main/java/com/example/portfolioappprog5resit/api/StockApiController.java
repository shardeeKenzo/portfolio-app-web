package com.example.portfolioappprog5resit.api;

import com.example.portfolioappprog5resit.api.dto.StockDto;
import com.example.portfolioappprog5resit.api.mapping.StockMapper;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockApiController {

    private static final Logger log = LoggerFactory.getLogger(StockApiController.class);

    private final StockService stockService;
    private final StockMapper mapper;

    public StockApiController(StockService stockService, StockMapper mapper) {
        this.stockService = stockService;
        this.mapper = mapper;
    }

    // GET /api/stocks?symbol=...&minPrice=...&maxPrice=...
    @GetMapping
    public ResponseEntity<?> getStocks(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice
    ) {
        List<Stock> stocks = stockService.getStocksByCriteria(symbol, minPrice, maxPrice);
        if (stocks.isEmpty()) return ResponseEntity.noContent().build(); // 204
        return ResponseEntity.ok(mapper.toDtoList(stocks));              // 200
    }

    // GET /api/stocks/{id}
    @GetMapping("{id}")
    public ResponseEntity<StockDto> getOne(@PathVariable int id) {
        Stock stock = stockService.findById(id);
        if (stock == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        return ResponseEntity.ok(mapper.toDto(stock));                                  // 200
    }

    // DELETE /api/stocks/{id}
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {

        log.info("[API] DELETE /api/stocks/{} invoked", id);

        Stock existing = stockService.findById(id);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        stockService.deleteById(id);                                                      // clears join rows first
        return ResponseEntity.noContent().build();                                        // 204
    }
}
