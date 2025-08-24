package com.example.portfolioappprog5resit.presentation.controller;

import com.example.portfolioappprog5resit.presentation.viewmodel.NewUserViewModel;
import com.example.portfolioappprog5resit.service.AppUserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
    private final AppUserService userService;
    public RegistrationController(AppUserService userService) { this.userService = userService; }

    @GetMapping("/register")
    public String show(Model model) {
        model.addAttribute("user", new NewUserViewModel());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid NewUserViewModel vm,
                           BindingResult br, HttpServletRequest request) throws ServletException {
        if (br.hasErrors() || !vm.getPassword1().equals(vm.getPassword2())) return "register";
        userService.createNewUser(vm.getUsername(), vm.getPassword1());
        request.login(vm.getUsername(), vm.getPassword1());
        return "redirect:/";
    }
}
