package com.revature.project1.controller;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.Address;
import com.revature.project1.Entities.User;
import com.revature.project1.repository.UserRepository;
import com.revature.project1.service.AddressService;
import com.revature.project1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/address")
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);


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
                logger.info("Fetching all addresses");
                return ResponseEntity.ok(addresses);
            } else{
                logger.warn("Unauthorized access attempt by user: {}", account.getAccountId());
                return ResponseEntity.ok("error: You have no permission to take this action!");
            }
        }else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
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
                    logger.error("Address not found for accountId: {}", account.getAccountId());
                    return ResponseEntity.ok("error: No address found!");
                } else {
                    logger.info("Fetching address by ID: {}", id);
                    return addressService.findAddressById(id)
                            .map(ResponseEntity::ok)
                            .orElse(ResponseEntity.notFound().build());
                }
            }
            User userVer = userRepository.findByAccount_accountId(account.getAccountId());
            if(userVer == null){
                //Validate if a user hasn't been created by this account
                logger.warn("User not found for account ID: {}", account.getAccountId());
                return ResponseEntity.ok("error: User not found !");
            } else if(userVer.getAddress().getAddressId() == null){
                //Validate if this user has created it's address
                logger.warn("User with account ID: {} does not own an address", account.getAccountId());
                return ResponseEntity.ok("error: This user does not own an Address");
            } else if(userVer.getAddress().getAddressId() == addressService.findAddressById(id).map(Address::getAddressId).orElse(null)){
                //Validate if this user is updating it's own address
                logger.info("Fetching address by ID: {}", id);
                return addressService.findAddressById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
            } else {
                logger.warn("User with account ID: {} does not own the address with ID: {}", account.getAccountId(), id);
                return ResponseEntity.ok("error: This user does not own this Address");
            }
        }else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
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
                logger.warn("User not found for account ID: {}", account.getAccountId());
                return ResponseEntity.ok("error: User not found !");
            //Validate if this user hasn't already created it's own address
            } else if (userVer.getAddress() == null ){
                logger.info("Creating a new address for user: {}", account.getAccountId());
                Address savedAddress = addressService.createAddress(address);
                //Assigning the created address to the user in session
                userVer.setAddress(savedAddress);
                userService.updateUser(userVer.getIdUser(),userVer);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedAddress);
            } else {
                logger.info("User with account ID: {} owns the address with ID: {}", account.getAccountId(), account.getUser().getAddress().getAddressId());
                return ResponseEntity.ok("error: This user already has an address");
            }
        }else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
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
                logger.info("Updating address for current user: {}", account.getAccountId());
                Address addressUpdated = addressService.updateAddress(id, addressDetails);
                return ResponseEntity.ok(addressUpdated);
            }
            User userVer = userRepository.findByAccount_accountId(account.getAccountId());
            if(userVer == null){
                //Validate if a user hasn't been created by this account
                logger.warn("User not found for account ID: {}", account.getAccountId());
                return ResponseEntity.ok("error: User not found !");
            } else if(userVer.getAddress() == null){
                //Validate if this user has created it's address
                logger.warn("User with account ID: {} does not own an address", account.getAccountId());
                return ResponseEntity.ok("error: This user does not own an Address");
            } else if(userVer.getAddress().getAddressId() == addressService.findAddressById(id).map(Address::getAddressId).orElse(null)){
                //Validate if this user is updating it's own address
                logger.info("Updating address for current user: {}", account.getAccountId());
                Address addressUpdated = addressService.updateAddress(id, addressDetails);
                return ResponseEntity.ok(addressUpdated);
            } else {
                logger.warn("User with account ID: {} does not own the address with ID: {}", account.getAccountId(), id);
                return ResponseEntity.ok("error: This user does not own this Address");
            }
        }else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
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
                logger.warn("Address with ID: {} not found for user with account ID: {}", account.getUser().getAddress().getAddressId(), account.getAccountId());
                return ResponseEntity.ok("error: Address not found !");
            }
            logger.info("Fetching address for current user: {}", account.getAccountId());
            Optional<Address> updatedAddress = addressService.getMyAddress(userFromDB.getAddress().getAddressId());
            return ResponseEntity.ok(updatedAddress);
        }
        else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
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
                logger.warn("Address with ID: {} not found for user with account ID: {}", account.getUser().getAddress().getAddressId(), account.getAccountId());
                return ResponseEntity.ok("error: Address not found !");
            }
            if (!Objects.equals(addressFromDB.getAddressId(),address.getAddressId())){
                logger.warn("User with account ID: {} does not own the address with ID: {}", account.getAccountId(), address.getAddressId());
                return ResponseEntity.ok("error: You cannot update another address !");
            }
            addressFromDB.setCountry(address.getCountry());
            addressFromDB.setState(address.getState());
            addressFromDB.setCity(address.getCity());
            addressFromDB.setStreet(address.getStreet());
            addressFromDB.setStreetNum(address.getStreetNum());
            addressFromDB.setZip(address.getZip());

            Address updatedAddress = addressService.updateAddress(addressFromDB.getAddressId(),addressFromDB);
            logger.info("Updating address for current user: {}", account.getAccountId());
            return ResponseEntity.ok(updatedAddress);
        }
        else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }
}
