package com.revature.project1.service;

import com.revature.project1.Entities.Loan;
import com.revature.project1.repository.LoanRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService{
    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public List<Loan> findAllLoan() {
        return loanRepository.findAll();
    }

    @Transactional()
    @Override
    public Loan createLoan(Loan loan) {
        return loanRepository.save(loan);
    }

    @Transactional
    @Override
    public Optional<Loan> updateLoan(Long id, Loan loanDetails){
     return loanRepository.findById(id).map(exitistingLoan->{
        exitistingLoan.setAmountRequested(loanDetails.getAmountRequested());
        exitistingLoan.setLastUpdate(loanDetails.getLastUpdate());
        exitistingLoan.setStatusReason(loanDetails.getStatusReason());
        exitistingLoan.setManagerUpdate(loanDetails.getManagerUpdate());
        exitistingLoan.setLoanStatus(loanDetails.getLoanStatus());
        exitistingLoan.setLoanType(loanDetails.getLoanType());
        return loanRepository.save(exitistingLoan);
     });
    }

    @Override
    public Optional<Loan> findLoanById(Long id){
        return loanRepository.findById(id);
    }

    @Override
    public List<Loan> findLoanByUserId(Long id){
        return loanRepository.findByUser_idUser(id);
    }

}
