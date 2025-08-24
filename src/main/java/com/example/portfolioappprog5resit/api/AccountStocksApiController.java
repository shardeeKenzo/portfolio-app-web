package com.example.portfolioappprog5resit.api;

import com.example.portfolioappprog5resit.api.mapping.StockMapper;
import com.example.portfolioappprog5resit.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts/{accountId}/stocks")
public class AccountStocksApiController {
    private final StockService stockService;
    private final StockMapper mapper;

    public AccountStocksApiController(StockService stockService, StockMapper mapper) {
        this.stockService = stockService;
        this.mapper = mapper;
    }

    // GET related: all stocks in an account
    @GetMapping
    public ResponseEntity<?> getForAccount(@PathVariable int accountId) {
        var list = stockService.findAllByAccountId(accountId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(mapper.toDtoList(list));
    }

    // GET helper: stocks not yet in this account
    @GetMapping("available")
    public ResponseEntity<?> getNotInAccount(@PathVariable int accountId) {
        var list = stockService.findAllNotInAccount(accountId);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(mapper.toDtoList(list));
    }
}
