package com.example.portfolioappprog5resit.api;

import com.example.portfolioappprog5resit.api.dto.InvestorCreateDto;
import com.example.portfolioappprog5resit.api.dto.InvestorDto;
import com.example.portfolioappprog5resit.domain.Investor;
import com.example.portfolioappprog5resit.service.InvestorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/investors")
public class InvestorRestController {

    private final InvestorService investorService;

    public InvestorRestController(InvestorService investorService) {
        this.investorService = investorService;
    }

    /** Simple search by name + optional dob (ISO yyyy-MM-dd). Returns list or empty. */
    @GetMapping
    public List<InvestorDto> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, name = "birthDate") String birthDate
    ) {
        var list = investorService.getInvestorsByCriteria(
                name == null ? "" : name,
                birthDate == null ? "" : birthDate   // ISO: yyyy-MM-dd
        );
        return list.stream().map(InvestorDto::from).toList();
    }

    /**
     * Create a new Investor.
     * SECURITY: this endpoint is deliberately open (permitAll) for testing the Client.
     */
    @PostMapping
    public ResponseEntity<InvestorDto> create(@Valid @RequestBody InvestorCreateDto in) {
        Investor i = new Investor();
        i.setName(in.name());
        i.setContactDetails(blankToNull(in.contactDetails()));
        i.setBirthDate(in.birthDate());
        i.setRiskProfile(blankToNull(in.riskProfile()));
        investorService.addInvestor(i);
        return ResponseEntity
                .created(URI.create("/api/investors/" + i.getId()))
                .body(InvestorDto.from(i));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}