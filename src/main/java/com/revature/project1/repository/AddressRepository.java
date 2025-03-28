package com.revature.project1.repository;

import com.revature.project1.Entities.Address;
import com.revature.project1.service.AddressService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
