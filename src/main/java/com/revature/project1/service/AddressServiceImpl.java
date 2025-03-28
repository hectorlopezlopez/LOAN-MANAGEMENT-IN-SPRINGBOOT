package com.revature.project1.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import com.revature.project1.Entities.Address;
import com.revature.project1.repository.AddressRepository;

import java.util.*;

@Service
public class AddressServiceImpl implements AddressService{
    private final AddressRepository addressRepository;

    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<Address> findAllAddress(){
        return addressRepository.findAll();
    }

    @Override
    public Optional<Address> findAddressById(Long id){
        return addressRepository.findById(id);
    }

    @Override
    public Address createAddress(Address address){
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Long id, Address addressDetails){
        Address existingAddress = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found with id: " + id));
        existingAddress.setCountry(addressDetails.getCountry());
        existingAddress.setState(addressDetails.getState());
        existingAddress.setCity(addressDetails.getCity());
        existingAddress.setStreet(addressDetails.getStreet());
        existingAddress.setStreetNum(addressDetails.getStreetNum());
        existingAddress.setZip(addressDetails.getZip());
        return addressRepository.save(existingAddress);
    }

    @Override
    public Optional<Address> getMyAddress(Long id){
        return addressRepository.findById(id);
    }
}
