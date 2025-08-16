package com.example.portfolioappprog5resit.service;

import java.util.List;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Investor;
import com.example.portfolioappprog5resit.exception.PortfolioApplicationException;
import com.example.portfolioappprog5resit.repository.InvestorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InvestorServiceImpl implements InvestorService {

    private final InvestorRepository investorRepository;

    public InvestorServiceImpl(InvestorRepository investorRepository) {
        this.investorRepository = investorRepository;
    }

    @Override
    @Transactional
    public void addInvestor(Investor investor) {
        try {
            investorRepository.save(investor);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to add investor", e);
        }
    }

    @Override
    @Transactional
    public void addAccount(Investor investor, BrokerageAccount brokerageAccount) {
        try {
            // link both ways
            brokerageAccount.setInvestor(investor);
            investor.getAccounts().add(brokerageAccount);

            // persist via cascade on Investor.accounts
            investorRepository.save(investor);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to add brokerage account", e);
        }
    }

    @Override
    public List<Investor> getAllInvestors() {
        try {
            return investorRepository.findAll();
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to retrieve investors", e);
        }
    }

    @Override
    public List<Investor> getInvestorsByCriteria(String nameInput, String dobInput) {
        try {
            return investorRepository.findByCriteria(nameInput, dobInput);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to filter investors by criteria", e);
        }
    }

    @Override
    public Investor findWithAccounts(int id) {
        try {
            return investorRepository.findWithAccounts(id);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to retrieve investor details", e);
        }
    }

    @Override
    @Transactional
    public void deleteInvestor(int id) {
        try {
            investorRepository.deleteById(id);
        } catch (Exception e) {
            throw new PortfolioApplicationException("Failed to delete investor", e);
        }
    }
}
