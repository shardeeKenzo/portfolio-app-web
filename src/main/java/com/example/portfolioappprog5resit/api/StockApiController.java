package com.example.portfolioappprog5resit.api;

import com.example.portfolioappprog5resit.api.dto.NewStockDto;
import com.example.portfolioappprog5resit.api.dto.StockDto;
import com.example.portfolioappprog5resit.api.dto.UpdateStockDto;
import com.example.portfolioappprog5resit.api.mapping.StockMapper;
import com.example.portfolioappprog5resit.config.security.CustomUserDetails;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.service.StockService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockApiController {

    private static final Logger log = LoggerFactory.getLogger(StockApiController.class);

    private final StockService stockService;
    private final StockMapper mapper;

    @Autowired
    public StockApiController(StockService stockService, StockMapper mapper) {
        this.stockService = stockService;
        this.mapper = mapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStocks(
            @RequestParam(required = false) String symbol,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice
    ) {
        List<Stock> stocks = stockService.getStocksByCriteria(symbol, minPrice, maxPrice);
        if (stocks.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(mapper.toDtoList(stocks));
    }

    // GET /api/stocks/{id}
    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockDto> getOne(@PathVariable int id) {
        Stock stock = stockService.findById(id);
        if (stock == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(mapper.toDto(stock));
    }

    // DELETE /api/stocks/{id}  (owner or admin)
    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable int id,
                                       @AuthenticationPrincipal CustomUserDetails me) {
        log.info("[API] DELETE /api/stocks/{} by {}", id, me != null ? me.getUsername() : "anonymous");
        Stock existing = stockService.findById(id);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        try {
            stockService.deleteByIdAuthorized(id, me);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StockDto> create(@RequestBody @Valid NewStockDto input,
                                           @AuthenticationPrincipal CustomUserDetails me) {

        if (me == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Stock entity = mapper.toEntity(input);
        stockService.addStock(entity, me.getUserId());
        URI location = URI.create("/api/stocks/" + entity.getId());
        return ResponseEntity.created(location).body(mapper.toDto(entity));
    }

    @PatchMapping(value = "{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patch(@PathVariable int id,
                                   @RequestBody @Valid UpdateStockDto input,
                                   @AuthenticationPrincipal CustomUserDetails me) {
        Stock existing = stockService.findById(id);
        if (existing == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        mapper.updateFromDto(input, existing);

        try {
            stockService.updateAuthorized(existing, me);
            return ResponseEntity.ok(mapper.toDto(existing));
        } catch (AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
