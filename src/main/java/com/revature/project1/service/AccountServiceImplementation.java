package com.revature.project1.service;

import com.revature.project1.Entities.Role.*;
import com.revature.project1.Entities.Account;
import com.revature.project1.repository.AccountRepository;
import com.revature.project1.repository.RoleRepository;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImplementation implements AccountService{

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    public AccountServiceImplementation(AccountRepository accountRepository, RoleRepository roleRepository) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Account createAccount(Account newAccount) {
        Long roleId = newAccount.getRole().getRoleId();
        newAccount.setPassword(BCrypt.withDefaults().hashToString(10, newAccount.getPassword().toCharArray()));
        if (accountRepository.existsByUsername(newAccount.getUsername())) {
            return new Account(null, newAccount.getRole());
        }
        else {
            newAccount.setRole(roleRepository.findById(roleId).orElse(null));
            if (newAccount.getRole() != null) {
                return accountRepository.save(newAccount);
            }
            return newAccount;
        }
    }

    @Override
    public Account loginUser(String username, String password) {
        return accountRepository.findByUsername(username);
    }
}