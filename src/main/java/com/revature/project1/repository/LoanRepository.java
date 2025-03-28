package com.revature.project1.repository;

import com.revature.project1.Entities.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long> {
    List<Loan> findByUser_idUser(Long idUser);

}
