package com.example.portfolioappprog5resit.repository;

import com.example.portfolioappprog5resit.domain.Investor;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvestorRepository extends JpaRepository<Investor, Integer> {

    @Query("""
           select distinct i
           from Investor i
           left join fetch i.accounts a
           where i.id = :id
           """)
    Optional<Investor> findWithAccounts(@Param("id") int id);

    List<Investor> findByNameContainingIgnoreCase(String name);
    List<Investor> findByBirthDate(LocalDate birthDate);
    List<Investor> findByNameContainingIgnoreCaseAndBirthDate(String name, LocalDate birthDate);

    default List<Investor> findByCriteria(String nameInput, String dobInput) {
        String name = (nameInput == null || nameInput.isBlank()) ? null : nameInput.trim();
        LocalDate dob = null;
        if (dobInput != null && !dobInput.isBlank()) {
            try {
                dob = LocalDate.parse(dobInput.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception ignore) { /* fall back to name-only search */ }
        }
        if (name != null && dob != null) {
            return findByNameContainingIgnoreCaseAndBirthDate(name, dob);
        } else if (name != null) {
            return findByNameContainingIgnoreCase(name);
        } else if (dob != null) {
            return findByBirthDate(dob);
        }
        return findAll();
    }
}
