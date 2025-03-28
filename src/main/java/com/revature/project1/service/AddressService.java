package com.revature.project1.service;

import com.revature.project1.Entities.Address;

import java.util.*;

public interface AddressService {
    List<Address> findAllAddress();
    Address createAddress(Address address);
    Optional<Address> findAddressById(Long id);
    Address updateAddress(Long id, Address addressDetails);
    Optional<Address> getMyAddress(Long idUser);
}
