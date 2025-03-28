package com.revature.project1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revature.project1.Entities.LoanStatus;

@Repository
public interface LoanStatusRepository extends JpaRepository<LoanStatus, Long> {
    
}
