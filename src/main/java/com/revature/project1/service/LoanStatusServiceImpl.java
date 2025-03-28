package com.revature.project1.service;
import com.revature.project1.repository.LoanStatusRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.project1.Entities.LoanStatus;

@Service
public class LoanStatusServiceImpl implements LoanStatusService{

    
    private final LoanStatusRepository loanStatusRepository;

    public LoanStatusServiceImpl(LoanStatusRepository loanStatusRepository){
        this.loanStatusRepository = loanStatusRepository;
    }

    @Override
    public List<LoanStatus> findAllLoanStatus(){
        return loanStatusRepository.findAll();
    }

    @Override
    public Optional<LoanStatus> findLoanStatusById(Long id){
        return loanStatusRepository.findById(id);
    }

    @Override
    public Optional<LoanStatus> updateLoanStatus(Long id, LoanStatus status){
        return loanStatusRepository.findById(id).map(existingStatus -> {
            existingStatus.setLoanStatus(status.getLoanStatus());
            return loanStatusRepository.save(existingStatus);
        });
    }
}
