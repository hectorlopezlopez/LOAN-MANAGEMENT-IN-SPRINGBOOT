package com.revature.project1.controller;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.revature.project1.Entities.Account;
import com.revature.project1.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {this.accountService = accountService;}

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> createAccount(@RequestBody Account account) {
        Account newAccount = accountService.createAccount(account);
        Map<String, Object> response = new HashMap<>();
        if (newAccount.getUsername() != null) {
            if (newAccount.getRole() != null) {
                response.put("message: ", "The account was successfully created!");
                response.put("account: ", newAccount);
                logger.info("New account registered: {}", newAccount.getUsername());
            }
            else {
                logger.warn("Attempt to register an account with an existing username: {}", account.getUsername());
                response.put("error: ", "Invalid role!");
            }
        }
        else {
            response.put("error: ", "This user account already exists!");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/sessionInfo")
    public ResponseEntity<?> getSessionAccount(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Account account = (Account) session.getAttribute("newAccount");
            if (account != null) {
                return ResponseEntity.ok(account);
            }
        }
        logger.warn("No active session found for request: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No session in progress");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Account account, HttpServletRequest servletRequest) {
        Map<String, Object> response = new HashMap<>();

        Account existingUser = accountService.loginUser(account.getUsername(), account.getPassword());

        if (existingUser != null &&
                BCrypt.verifyer().verify(account.getPassword().toCharArray(), existingUser.getPassword()).verified) {

            HttpSession oldSession = servletRequest.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            HttpSession newSession = servletRequest.getSession(true);
            newSession.setAttribute("newAccount", existingUser);

            response.put("message: ", "Successful login!");
            response.put("account: ", existingUser);
            logger.info("User logged: {}, Id: {}", existingUser.getUsername(), existingUser.getAccountId());
        } else {
            response.put("error: ", "Invalid credentials!");
            logger.warn("Failed login attempt for username: {}", account.getUsername());
        }

        return ResponseEntity.ok(response);
    }


    @GetMapping("/logout")
    public ResponseEntity<HashMap<String, String>> logout(HttpServletRequest servletRequest) {
        HashMap<String, String> response = new HashMap<>();
        if (servletRequest.getSession(false) != null) {
            servletRequest.getSession(false).invalidate();
            response.put("message: ", "Successful logout!");
            logger.info("User logged out successfully");
        }
        else {
            response.put("error: ", "Invalid action (no session in progress)!");
            logger.warn("Logout attempt with no active session");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}