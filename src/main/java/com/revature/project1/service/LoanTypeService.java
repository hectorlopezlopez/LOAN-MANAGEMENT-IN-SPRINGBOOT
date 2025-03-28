package com.revature.project1.service;

import java.util.List;
import java.util.Optional;

import com.revature.project1.Entities.LoanType;

public interface LoanTypeService {
    LoanType createLoanType(LoanType loanType);
    List<LoanType> findAllLoanTypes();
    Optional<LoanType> findLoanTypeById(Long id);
    Optional<LoanType> updateLoanType(Long id, LoanType type);
}
