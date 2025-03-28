package com.revature.project1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revature.project1.Entities.LoanStatus;
import com.revature.project1.service.LoanStatusService;


@RestController
@RequestMapping("/loanStatus")
public class LoanStatusController {


    private final LoanStatusService loanStatusService;

    public LoanStatusController(LoanStatusService loanStatusService){
        this.loanStatusService = loanStatusService;
    }

    @GetMapping
    public ResponseEntity<List<LoanStatus>> getAllLoanStatus(){
        return ResponseEntity.ok(loanStatusService.findAllLoanStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoanStatus> getStatusById(@PathVariable Long id){
        return loanStatusService.findLoanStatusById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanStatus> updateLoanStatus(@PathVariable Long id, @RequestBody LoanStatus status){
        return loanStatusService.updateLoanStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    
}
