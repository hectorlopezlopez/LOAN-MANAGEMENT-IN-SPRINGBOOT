package com.revature.project1.repository;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {}