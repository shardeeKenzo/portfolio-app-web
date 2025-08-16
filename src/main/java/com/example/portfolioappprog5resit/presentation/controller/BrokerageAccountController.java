package com.example.portfolioappprog5resit.presentation.controller;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Stock;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.service.BrokerageAccountService;
import com.example.portfolioappprog5resit.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/accounts")
public class BrokerageAccountController {

    private final Logger logger = LoggerFactory.getLogger(BrokerageAccountController.class);
    private final BrokerageAccountService brokerageAccountService;
    private final StockService stockService;

    @Autowired
    public BrokerageAccountController(BrokerageAccountService brokerageAccountService, StockService stockService) {
        this.brokerageAccountService = brokerageAccountService;
        this.stockService = stockService;
    }

    /**
     * GET /accounts/{id} -> Show account details along with available stocks to add
     */
    @GetMapping("/{id}")
    public String showAccountDetails(@PathVariable int id, Model model) {
        logger.info("Request for account details with id={}", id);
        BrokerageAccount account = brokerageAccountService.findById(id);
        if (account == null) {
            throw new PortfolioApplicationException("Account not found with ID: " + id);
        }
        model.addAttribute("account", account);

        // fetch available stocks not already in the account
        List<Stock> availableStocks = stockService.getAllStocks().stream()
                .filter(stock -> !account.getStocks().contains(stock))
                .toList();
        model.addAttribute("availableStocks", availableStocks);

        return "accountdetails";
    }

    /**
     * POST /accounts/{id}/addstocks -> Add selected stocks to the account
     */
    @PostMapping("/{id}/addstocks")
    public String addStocksToAccount(@PathVariable int id, @RequestParam(required = false) List<Integer> stockIds, RedirectAttributes redirectAttributes) {
        logger.info("Adding stocks {} to account {}", stockIds, id);
        BrokerageAccount account = brokerageAccountService.findById(id);
        if (account == null) {
            throw new PortfolioApplicationException("Account not found with ID: " + id);
        }

        if (stockIds == null || stockIds.isEmpty()) {
            redirectAttributes.addAttribute("id", id);
            redirectAttributes.addAttribute("error", "noStocksSelected");
            return "redirect:/accounts/{id}";
        }

        List<Stock> stocksToAdd = stockService.findByIds(stockIds);
        brokerageAccountService.addStocksToAccount(account, stocksToAdd);

        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addAttribute("success", "stocksAdded");
        return "redirect:/accounts/{id}";
    }

    /**
     * POST /accounts/{id}/removestock -> Remove a stock from the account
     */
    @PostMapping("/{id}/removestock")
    public String removeStockFromAccount(@RequestParam int stockId, @PathVariable int id, RedirectAttributes redirectAttributes) {
        logger.info("Removing stock {} from account {}", stockId, id);
        BrokerageAccount account = brokerageAccountService.findById(id);
        if (account == null) {
            throw new PortfolioApplicationException("Account not found with ID: " + id);
        }

        Stock stock = stockService.findById(stockId);
        if (stock == null) {
            throw new PortfolioApplicationException("Stock not found with ID: " + stockId);
        }

        brokerageAccountService.removeStockFromAccount(account, stock);

        redirectAttributes.addAttribute("id", id);
        redirectAttributes.addAttribute("success", "stockRemoved");
        return "redirect:/accounts/{id}";
    }

    /**
     * POST /accounts/delete/{id} -> Delete a brokerage account
     */
    @PostMapping("/delete/{id}")
    public String deleteAccount(@PathVariable int id, RedirectAttributes redirectAttributes) {
        logger.info("Deleting brokerage account with id={}", id);
        try {
            brokerageAccountService.deleteById(id);
            redirectAttributes.addAttribute("success", "accountDeleted");
        } catch (PortfolioApplicationException e) {
            redirectAttributes.addAttribute("error", "accountDeletionFailed");
            logger.error("Failed to delete brokerage account with id={}", id, e);
        }
        return "redirect:/accounts";
    }
}
