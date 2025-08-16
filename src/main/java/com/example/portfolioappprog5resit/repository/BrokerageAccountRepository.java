package com.example.portfolioappprog5resit.repository;

import com.example.portfolioappprog5resit.domain.BrokerageAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BrokerageAccountRepository extends JpaRepository<BrokerageAccount, Integer> {

    /** Load account with its stocks in one roundtrip (avoid N+1). */
    @Query("""
           select ba
           from BrokerageAccount ba
           left join fetch ba.investor
           left join fetch ba.stocks s
           where ba.id = :id
           """)
    BrokerageAccount findByIdWithStocks(@Param("id") int id);
    // deleteById, findAll, save, findById already inherited
}
