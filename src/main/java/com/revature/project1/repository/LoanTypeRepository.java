package com.revature.project1.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.revature.project1.Entities.LoanType;


@Repository
public interface LoanTypeRepository extends JpaRepository<LoanType, Long> {
    
}
