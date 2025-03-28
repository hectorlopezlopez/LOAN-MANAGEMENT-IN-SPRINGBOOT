package com.revature.project1.service;

import java.util.List;
import java.util.Optional;

import com.revature.project1.Entities.LoanStatus;

public interface LoanStatusService {
    List<LoanStatus> findAllLoanStatus();
    Optional<LoanStatus> findLoanStatusById(Long id);
    Optional<LoanStatus> updateLoanStatus(Long id, LoanStatus status);

}
