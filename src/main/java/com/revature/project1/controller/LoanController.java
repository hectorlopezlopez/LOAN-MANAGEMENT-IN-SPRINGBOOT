package com.revature.project1.controller;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.Loan;
import com.revature.project1.Entities.User;
import com.revature.project1.repository.UserRepository;
import com.revature.project1.service.LoanService;
import com.revature.project1.service.UserService;
import com.revature.project1.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/loan")
public class LoanController {
    private final LoanService loanService;
    private final UserRepository userRepository;
    private final UserServiceImpl userService;

    public LoanController(LoanService loanService, UserRepository userRepository,UserServiceImpl userService) {
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getAllLoans(HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if (account.getRole().getRoleId() == 2){ // Only manager can see all Loans
                return ResponseEntity.ok(loanService.findAllLoan());
            } else {
                return ResponseEntity.ok("error: You have no permission to take this action!");
            }
        }
        return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getLoansByUserId(HttpServletRequest httpServletRequest){


        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userFromDB = userService.findByAccountId(account.getAccountId());
            if (userFromDB==null ){
                return ResponseEntity.ok("error: User not found !");
            }

            //if (account.getUser().getIdUser() == id){
                return ResponseEntity.ok(loanService.findLoanByUserId(userFromDB.getIdUser()));
            //} else {
             //   return ResponseEntity.ok("error: You have no permission to take this action!");
            //}
        }
        return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getSession(false) == null) {
            return jsonError("Invalid action (no session is in progress)!");
        }

        HttpSession httpSession = httpServletRequest.getSession(false);
        Account account = (Account) httpSession.getAttribute("newAccount");

        if (account.getRole().getRoleId() != 2) {
            return jsonError("You have no permission to take this action!");
        }

        User userFromDB = userRepository.findByAccount_accountId(account.getAccountId());

        Optional<Loan> optionalLoan = loanService.findLoanById(id);
        if (optionalLoan.isEmpty()) {
            return jsonError("This loan does not exist");
        }


        return ResponseEntity.ok(optionalLoan.get());
    }

    private ResponseEntity<Map<String, String>> jsonError(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return ResponseEntity.badRequest().body(error);
    }


    @PostMapping
    public ResponseEntity<?> createLoan(@RequestBody Loan loan, HttpServletRequest httpServletRequest) {
        HttpSession httpSession = httpServletRequest.getSession(false);
        if (httpSession == null) {
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
        Account account = (Account) httpSession.getAttribute("newAccount");
        if (account == null) {
            return ResponseEntity.ok("error: No account found in session!");
        }
        if (account.getRole().getRoleId() != 1) {
            return ResponseEntity.ok("error: You have no permission to take this action!");
        }
        User user = userRepository.findByAccount_accountId(account.getAccountId());
        if (user == null) {
            return ResponseEntity.ok("error: User does not exist!");
        }
        loan.setUser(user);
        Loan loanCreated = loanService.createLoan(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(loanCreated);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLoan(@PathVariable Long id, @RequestBody Loan loanDetails, HttpServletRequest httpServletRequest){
        if(httpServletRequest.getSession(false)!= null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if(account.getRole().getRoleId() == 2){ //only manager can update
                return loanService.updateLoan(id, loanDetails)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.ok("error: As a user you can only create a loan!");
            }
        } else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }
}
