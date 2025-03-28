package com.revature.project1.repository;

import com.revature.project1.Entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByUsername(String username);
    Account findByUsername(String username);
}