package com.revature.project1.controller;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.Address;
import com.revature.project1.Entities.User;
import com.revature.project1.repository.UserRepository;
import com.revature.project1.service.AddressService;
import com.revature.project1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final UserService userService;

    public AddressController(AddressService addressService, UserService userService, UserRepository userRepository, UserService userService1) {
        this.addressService = addressService;
        this.userRepository = userRepository;
        this.userService = userService1;
    }

    @GetMapping
    public ResponseEntity<Object> getAllAddress(HttpServletRequest httpServletRequest){
        if(httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if(account.getRole().getRoleId() == 2) { //Is a manager, only manager can see all addresses
                List<Address> addresses = addressService.findAllAddress();
                return ResponseEntity.ok(addresses);
            } else{
                return ResponseEntity.ok("error: You have no permission to take this action!");
            }
        }else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAddressById(@PathVariable Long id, HttpServletRequest httpServletRequest){
        if(httpServletRequest.getSession(false) != null){
            //Validate if a session is in progress
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if(account.getRole().getRoleId() == 2) {
                //Is a manager
                if(addressService.findAddressById(id).isEmpty()){
                    return ResponseEntity.ok("error: No address found!");
                } else {
                    return addressService.findAddressById(id)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                }
            }
            User userVer = userRepository.findByAccount_accountId(account.getAccountId());
            if(userVer == null){
                //Validate if a user hasn't been created by this account
                return ResponseEntity.ok("error: User not found !");
            } else if(userVer.getAddress().getAddressId() == null){
                //Validate if this user has created it's address
                return ResponseEntity.ok("error: This user does not own an Address");
            } else if(userVer.getAddress().getAddressId() == addressService.findAddressById(id).map(Address::getAddressId).orElse(null)){
                //Validate if this user is updating it's own address
                return addressService.findAddressById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
            } else {
                return ResponseEntity.ok("error: This user does not own this Address");
            }
        }else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @PostMapping
    public ResponseEntity<?> createAddress(@RequestBody Address address, HttpServletRequest httpServletRequest){
        if(httpServletRequest.getSession(false) != null){
            //Validate if a session is in progress
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userVer = (User) userRepository.findByAccount_accountId(account.getAccountId());
            //Validate if a user hasn't been created by this account
            if(userVer == null){
                return ResponseEntity.ok("error: User not found !");
            //Validate if this user hasn't already created it's own address
            } else if (userVer.getAddress() == null ){
                Address savedAddress = addressService.createAddress(address);
                //Assigning the created address to the user in session
                userVer.setAddress(savedAddress);
                userService.updateUser(userVer.getIdUser(),userVer);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
            } else {
                return ResponseEntity.ok("error: This user already has an address");
            }
        }else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Long id, @RequestBody Address addressDetails, HttpServletRequest httpServletRequest){
        if(httpServletRequest.getSession(false) != null){
            //Validate if a session is in progress
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if(account.getRole().getRoleId() == 2) {
                //Is a manager, can update any address
                Address addressUpdated = addressService.updateAddress(id, addressDetails);
                return ResponseEntity.ok(addressUpdated);
            }
            User userVer = userRepository.findByAccount_accountId(account.getAccountId());
            if(userVer == null){
                //Validate if a user hasn't been created by this account
                return ResponseEntity.ok("error: User not found !");
            } else if(userVer.getAddress() == null){
                //Validate if this user has created it's address
                return ResponseEntity.ok("error: This user does not own an Address");
            } else if(userVer.getAddress().getAddressId() == addressService.findAddressById(id).map(Address::getAddressId).orElse(null)){
                //Validate if this user is updating it's own address
                Address addressUpdated = addressService.updateAddress(id, addressDetails);
                return ResponseEntity.ok(addressUpdated);
            } else {
                return ResponseEntity.ok("error: This user does not own this Address");
            }
        }else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @GetMapping("/myAddress")
    public ResponseEntity<?> getMyAddress(HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userFromDB = userRepository.findByAccount_accountId(account.getAccountId());
            if (userFromDB.getAddress()==null ){
                return ResponseEntity.ok("error: Address not found !");
            }
            Optional<Address> updatedAddress = addressService.getMyAddress(userFromDB.getAddress().getAddressId());
            return ResponseEntity.ok(updatedAddress);
        }
        else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @PutMapping("/myAddress")
    public ResponseEntity<?> updateMyAddress(@RequestBody Address address, HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userFromDB = userRepository.findByAccount_accountId(account.getAccountId());
            Address addressFromDB = userFromDB.getAddress();

            if (addressFromDB==null ){
                return ResponseEntity.ok("error: Address not found !");
            }
            if (!Objects.equals(addressFromDB.getAddressId(),address.getAddressId())){
                return ResponseEntity.ok("error: You cannot update another address !");
            }
            addressFromDB.setCountry(address.getCountry());
            addressFromDB.setState(address.getState());
            addressFromDB.setCity(address.getCity());
            addressFromDB.setStreet(address.getStreet());
            addressFromDB.setStreetNum(address.getStreetNum());
            addressFromDB.setZip(address.getZip());

            Address updatedAddress = addressService.updateAddress(addressFromDB.getAddressId(),addressFromDB);
            return ResponseEntity.ok(updatedAddress);
        }
        else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }
}
