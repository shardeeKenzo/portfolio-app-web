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

    @GetMapping("/{id}")
    public String showAccountDetails(@PathVariable int id, Model model) {
        logger.info("Request for account details with id={}", id);
        BrokerageAccount account = brokerageAccountService.findByIdWithStocks(id);
        if (account == null) {
            throw new PortfolioApplicationException("Account not found with ID: " + id);
        }
        model.addAttribute("account", account);

        // Best: fetch “available” stocks with a repo query (no in-memory contains)
        List<Stock> availableStocks = stockService.findAllNotInAccount(id);
        model.addAttribute("availableStocks", availableStocks);

        return "accountdetails";
    }

    @PostMapping("/{id}/addstocks")
    public String addStocksToAccount(@PathVariable int id,
                                     @RequestParam(required = false) List<Integer> stockIds,
                                     RedirectAttributes ra) {
        if (stockIds == null || stockIds.isEmpty()) {
            ra.addAttribute("id", id);
            ra.addAttribute("error", "noStocksSelected");
            return "redirect:/accounts/{id}";
        }
        brokerageAccountService.addStocksToAccount(id, stockIds);
        ra.addAttribute("id", id);
        ra.addAttribute("success", "stocksAdded");
        return "redirect:/accounts/{id}";
    }

    @PostMapping("/{id}/removestock")
    public String removeStockFromAccount(@PathVariable int id,
                                         @RequestParam int stockId,
                                         RedirectAttributes ra) {
        brokerageAccountService.removeStockFromAccount(id, stockId);
        ra.addAttribute("id", id);
        ra.addAttribute("success", "stockRemoved");
        return "redirect:/accounts/{id}";
    }

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
