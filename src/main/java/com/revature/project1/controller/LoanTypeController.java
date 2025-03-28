package com.revature.project1.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.project1.Entities.LoanType;
import com.revature.project1.service.LoanTypeService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/loanType")
public class LoanTypeController {

    
    private final LoanTypeService loanTypeService;

    public LoanTypeController(LoanTypeService loanTypeService){
        this.loanTypeService = loanTypeService;
    }

    @GetMapping
    public ResponseEntity<List<LoanType>> getAllLoanTypes(){
        return ResponseEntity.ok(loanTypeService.findAllLoanTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanType> getLoanTypeById(@PathVariable Long id) {
        return loanTypeService.findLoanTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanType> updateLoan(@PathVariable Long id, @RequestBody LoanType type){
        return loanTypeService.updateLoanType(id, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<LoanType> createLoanType(@RequestBody LoanType loanType){
        LoanType newType = loanTypeService.createLoanType(loanType);
        return ResponseEntity.status(HttpStatus.CREATED).body(newType);
    }
    
    
}
