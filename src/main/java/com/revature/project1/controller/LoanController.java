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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/loan")
public class LoanController {
    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

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
                logger.info("Manager requested all loans");
                return ResponseEntity.ok(loanService.findAllLoan());
            } else {
                logger.warn("Unauthorized action (getAllLoans) attempt by user: {} with role ID: {}", account.getUsername(), account.getRole().getRoleId());
                return ResponseEntity.ok("error: You have no permission to take this action!");
            }
        }
        logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
        return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
    }

    @GetMapping("/user")
    public ResponseEntity<?> getLoansByUserId(HttpServletRequest httpServletRequest){


        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userFromDB = userService.findByAccountId(account.getAccountId());
            if (userFromDB==null ){
                logger.error("User not found for accountId: {}", account.getAccountId());
                return ResponseEntity.ok("error: User not found !");
            }
            return ResponseEntity.ok(loanService.findLoanByUserId(userFromDB.getIdUser()));
        }
        logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
        return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLoanById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getSession(false) == null) {
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return jsonError("Invalid action (no session is in progress)!");
        }
        HttpSession httpSession = httpServletRequest.getSession(false);
        Account account = (Account) httpSession.getAttribute("newAccount");
        if (account.getRole().getRoleId() != 2) {
            logger.warn("Unauthorized action (getLoanById) attempt by user: {} with role ID: {}", account.getUsername(), account.getRole().getRoleId());
            return jsonError("You have no permission to take this action!");
        }
        User userFromDB = userRepository.findByAccount_accountId(account.getAccountId());
        Optional<Loan> optionalLoan = loanService.findLoanById(id);
        if (optionalLoan.isEmpty()) {
            logger.error("Loan ID: {} does not exist", id);
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
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
        Account account = (Account) httpSession.getAttribute("newAccount");
        if (account == null) {
            logger.warn("No account found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: No account found in session!");
        }
        if (account.getRole().getRoleId() != 1) {
            logger.warn("Unauthorized action (create loan) attempt by user: {} with role ID: {}", account.getUsername(), account.getRole().getRoleId());
            return ResponseEntity.ok("error: You have no permission to take this action!");
        }
        User user = userRepository.findByAccount_accountId(account.getAccountId());
        if (user == null) {
            logger.error("User not found for accountId: {}", account.getAccountId());
            return ResponseEntity.ok("error: User does not exist!");
        }
        loan.setUser(user);
        Loan loanCreated = loanService.createLoan(loan);
        logger.info("New loan registered: ID: {}", loanCreated.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(loanCreated);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLoan(@PathVariable Long id, @RequestBody Loan loanDetails, HttpServletRequest httpServletRequest){
        if(httpServletRequest.getSession(false)!= null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if(account.getRole().getRoleId() == 2){ //only manager can update
                logger.info("Updating loan ID: {} by manager: {}", id, account.getUsername());
                return loanService.updateLoan(id, loanDetails)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
            } else {
                logger.warn("Unauthorized action (update loan) attempt by user: {} with role ID: {}", account.getUsername(), account.getRole().getRoleId());
                return ResponseEntity.ok("error: As a user you can only create a loan!");
            }
        } else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }
}
