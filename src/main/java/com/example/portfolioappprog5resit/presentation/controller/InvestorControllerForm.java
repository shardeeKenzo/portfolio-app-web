package com.example.portfolioappprog5resit.presentation.controller;

import com.example.portfolioappprog5resit.domain.Investor;
import com.example.portfolioappprog5resit.presentation.viewmodel.InvestorForm;
import com.example.portfolioappprog5resit.service.InvestorService;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/investors")
@SessionAttributes("investorForm")
public class InvestorControllerForm {

    private InvestorService investorService;


    public InvestorControllerForm(InvestorService investorService) { this.investorService = investorService; }

    @GetMapping("/add")
    public String showAddInvestorForm(Model model) {
        model.addAttribute("investorForm", new InvestorForm());
        return "addinvestor";
    }

    @PostMapping("/add")
    public String addInvestor(@Valid @ModelAttribute("investorForm") InvestorForm form,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "addinvestor";
        }
        Investor investor = convertFormToDomain(form);
        investorService.addInvestor(investor);
        return "redirect:/investors";
    }

    private Investor convertFormToDomain(InvestorForm form) {
        Investor investor = new Investor();
        investor.setName(form.getName());
        investor.setContactDetails(form.getContactDetails());
        investor.setBirthDate(form.getBirthDate());
        investor.setRiskProfile(form.getRiskProfile());
        return investor;
    }
}
