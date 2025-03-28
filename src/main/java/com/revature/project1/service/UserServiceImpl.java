package com.revature.project1.service;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.Address;
import com.revature.project1.Entities.User;
import com.revature.project1.repository.AccountRepository;
import com.revature.project1.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{


    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public UserServiceImpl(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional()
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getMyUserInfo(Long id){
        return userRepository.findById(id);
    }

    @Transactional
    @Override
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        validateUserFields(user);

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setCreatedAt(user.getCreatedAt());
        existingUser.setPhone(user.getPhone());

        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }

        if (user.getAccount() != null) {
            existingUser.setAccount(user.getAccount());
        }

        if (user.getLoans() != null && !user.getLoans().isEmpty()) {
            existingUser.getLoans().clear();
            existingUser.getLoans().addAll(user.getLoans());
            existingUser.getLoans().forEach(loan -> loan.setUser(existingUser));
        }

        return userRepository.save(existingUser);
    }

    public User findByAccountId(Long accountId) {
        return userRepository.findByAccount_accountId(accountId);
    }



    private void validateUserFields(User user) {
        if (user.getFirstName() == null)
            throw new IllegalArgumentException("Field 'firstName' must not be null");
        if (user.getLastName() == null)
            throw new IllegalArgumentException("Field 'lastName' must not be null");
        if (user.getCreatedAt() == null)
            throw new IllegalArgumentException("Field 'createdAt' must not be null");
        if (user.getEmail() == null)
            throw new IllegalArgumentException("Field 'email' must not be null");
        if (user.getPhone() == null)
            throw new IllegalArgumentException("Field 'phone' must not be null");
    }


    @Transactional
    @Override
    public User updateUserPartial(Long id, Map<String, Object> updates) {
        return userRepository.findById(id).map(existingUser -> {

            updates.forEach((field, valor) -> {
                if (valor == null) {
                    throw new RuntimeException("The field '" + field + "' cannot be null.");
                }
                switch (field) {
                    case "firstName":
                        existingUser.setFirstName((String) valor);
                        break;
                    case "lastName":
                        existingUser.setLastName((String) valor);
                        break;
                    case "email":
                        existingUser.setEmail((String) valor);
                        break;
                    case "createdAt":
                        existingUser.setCreatedAt((String) valor);
                        break;
                    case "address":
                        existingUser.setAddress((Address) valor);
                        break;
                    case "account":
                        existingUser.setAccount((Account) valor);
                        break;
                    case "phone":
                        existingUser.setPhone((String) valor);
                        break;/*
                    case "idRol":
                        Role rol = rolRepository.findById(Long.parseLong(valor.toString()))
                                .orElseThrow(() -> new RuntimeException("Rol not found"));
                        existingUser.setRol(rol);
                        break;*/
                    case "idAccount":
                        Account account = accountRepository.findById(Long.parseLong(valor.toString()))
                                .orElseThrow(() -> new RuntimeException("Account not found"));
                        existingUser.setAccount(account);
                        break;
                }
            });

            return userRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

}
