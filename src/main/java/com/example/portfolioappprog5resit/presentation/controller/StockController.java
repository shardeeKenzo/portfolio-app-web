package com.example.portfolioappprog5resit.presentation.controller;

import com.example.portfolioappprog5resit.config.security.CustomUserDetails;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stocks")
public class StockController {

    private final Logger logger = LoggerFactory.getLogger(StockController.class);
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /**
     * GET /stocks -> Show all stocks
     */
    @GetMapping
    public String showStocksView(Model model){
        logger.info("Request for stocks view!");
        model.addAttribute("stocks", stockService.getAllStocks());
        return "stocks";
    }

    /**
     * GET /stocks/addstock -> Show add form
     */
    @GetMapping("/addstock")
    public String showAddStockForm(Model model) {
        model.addAttribute("stock", new Stock());
        return "addstock";
    }

    /**
     * POST /stocks/filter -> Filter stocks based on criteria
     */
    @GetMapping("/filter")
    public String filterStocks(@RequestParam(required = false) String symbol,
                               @RequestParam(required = false) String minPriceInput,
                               @RequestParam(required = false) String maxPriceInput,
                               Model model) {
        model.addAttribute("stocks", stockService.getStocksByCriteria(symbol, minPriceInput, maxPriceInput));
        return "stocks";
    }

    /**
     * POST /stocks/addstock -> Add a new stock
     */
    @PostMapping("/addstock")
    public String addStock(Stock stock, @AuthenticationPrincipal CustomUserDetails me) {
        stockService.addStock(stock, me.getUserId());             // ← creator locked in here
        return "redirect:/stocks";
    }

    /**
     * GET /stocks/{id} -> Show stock details
     */
    @GetMapping("/{id}")
    public String showStockDetails(@PathVariable int id, Model model) {
        Stock stock = stockService.findByIdWithCreator(id);
        if (stock == null) throw new PortfolioApplicationException("Stock not found with ID: " + id);
        model.addAttribute("stock", stock);
        return "stockdetails";
    }

    /**
     * POST /stocks/delete/{id} -> Delete a stock
     */
    @PostMapping("/delete/{id}")
    public String deleteStock(@PathVariable int id, @AuthenticationPrincipal CustomUserDetails me) {
        stockService.deleteByIdAuthorized(id, me);                // ← owner/admin check
        return "redirect:/stocks";
    }
}
