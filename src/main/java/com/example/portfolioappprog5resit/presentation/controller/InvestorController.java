package com.example.portfolioappprog5resit.presentation.controller;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Investor;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.service.InvestorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/investors")
public class InvestorController {

    private Logger logger = LoggerFactory.getLogger(InvestorController.class);
    private InvestorService investorService;
    public InvestorController(InvestorService investorService) {
        this.investorService = investorService;
    }

    @GetMapping
    public String showInvestorsView(Model model){
        logger.info("Request for investors view!");
        model.addAttribute("investors", investorService.getAllInvestors());
        return "investors";
    }
    @GetMapping("/addinvestor")
    public String showAddInvestorForm(Model model) {
        model.addAttribute("investor", new Investor());
        return "addinvestor";
    }
    @PostMapping("/filter")
    public String filterInvestors(@RequestParam String name, @RequestParam(required = false) String birthDate, Model model) {
        logger.info("Extracting investors based on criteria...");
        model.addAttribute("investors", investorService.getInvestorsByCriteria(name, birthDate));
        return "investors";
    }
    @PostMapping("/addinvestor")
    public String addInvestor(@ModelAttribute("investor") Investor investor) {
        investorService.addInvestor(investor);
        logger.info("Investor {} added", investor);
        return "redirect:/investors";
    }

    @GetMapping("/{id}")
    public String getInvestorDetails(@PathVariable int id, Model model) {
        Investor investor = investorService.findWithAccounts(id);
        if (investor == null) {
            throw new PortfolioApplicationException("Investor not found with ID: " + id);
        }
        model.addAttribute("investor", investor);
        return "investordetails";
    }

    @GetMapping("/{id}/addaccount")
    public String showAddAccountForm(@PathVariable int id, Model model) {
        Investor investor = investorService.findWithAccounts(id);
        if (investor == null) {
            throw new PortfolioApplicationException("Investor not found with ID: " + id);
        }
        BrokerageAccount account = new BrokerageAccount();
        account.setInvestor(investor);
        model.addAttribute("account", account);
        model.addAttribute("investor", investor);
        return "addaccount";
    }

    @PostMapping("/{id}/addaccount")
    public String addAccount(
            @PathVariable int id,
            @ModelAttribute("account") @Valid BrokerageAccount account,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            Investor investor = investorService.findWithAccounts(id);
            model.addAttribute("investor", investor);
            return "addaccount";
        }
        Investor investor = investorService.findWithAccounts(id);
        if (investor == null) {
            throw new PortfolioApplicationException("Investor not found with ID: " + id);
        }
        account.setCreationDate(java.time.LocalDate.now());
        investorService.addAccount(investor, account);
        return "redirect:/investors/" + id + "?success=accountAdded";
    }


    @PostMapping("/delete/{id}")
    public String deleteInvestor(@PathVariable int id) {
        investorService.deleteInvestor(id);
        return "redirect:/investors";
    }

}
