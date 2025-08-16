package com.example.portfolioappprog5resit.service;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import com.example.portfolioappprog5resit.domain.Investor;

import java.util.List;

public interface InvestorService {
    void addInvestor(Investor investor);
    void addAccount(Investor investor, BrokerageAccount brokerageAccount);
    List<Investor> getAllInvestors();
    List<Investor> getInvestorsByCriteria(String nameInput, String dobInput);
    Investor findWithAccounts(int id);
    void deleteInvestor(int id);
}
