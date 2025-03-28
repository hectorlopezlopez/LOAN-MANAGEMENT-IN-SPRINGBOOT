package com.revature.project1.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.revature.project1.Entities.LoanType;

import com.revature.project1.repository.LoanTypeRepository;

@Service
public class LoanTypeServiceImpl implements LoanTypeService{

    
    private final LoanTypeRepository loanTypeRepository;

    public LoanTypeServiceImpl(LoanTypeRepository loanTypeRepository){
        this.loanTypeRepository = loanTypeRepository;
    }

    @Override
    public LoanType createLoanType(LoanType loanType){
        return loanTypeRepository.save(loanType);
    }

    @Override
    public List<LoanType> findAllLoanTypes(){
        return loanTypeRepository.findAll();
    }

    @Override
    public Optional<LoanType> findLoanTypeById(Long id){
        return loanTypeRepository.findById(id);
    }

    @Override
    public Optional<LoanType> updateLoanType(Long id, LoanType type){
        return loanTypeRepository.findById(id).map(existingType -> {
            existingType.setLoanType(type.getLoanType());
            return loanTypeRepository.save(existingType);
        });
    }
}
