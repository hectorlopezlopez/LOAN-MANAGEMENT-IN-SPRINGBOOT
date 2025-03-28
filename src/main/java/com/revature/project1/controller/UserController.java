package com.revature.project1.controller;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.User;
import com.revature.project1.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if (account.getRole().getRoleId() == 2) {
                List<User> users = userService.getUsers();
                logger.info("Manager requested all users");
                return ResponseEntity.ok(users);
            }
            else {
                logger.warn("Unauthorized action (getUsers) attempt by user: {} with role ID: {}", account.getUsername(), account.getRole().getRoleId());
                return ResponseEntity.ok("error: You have no permission to take this action!");
            }
        }
        else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id , HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null) {
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if (account.getRole().getRoleId() == 2) {
                logger.info("Manager requested loans by ID: {}", id);
                Optional<User> user = userService.getUserById(id);
                if(user.isPresent()){
                    return ResponseEntity.ok(user.orElseThrow());
                }else{
                    logger.error("User not found for accountId: {}", account.getAccountId());
                    return ResponseEntity.ok("error: No user found!");
                }
            } else {
                logger.warn("Unauthorized action (getUserById) attempt by user: {} with role ID: {}", account.getUsername(), account.getRole().getRoleId());
                return ResponseEntity.ok("error: You have no permission to take this action!");

            }
        }else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userFromDB = (User) userService.findByAccountId(account.getAccountId());
            //Validate if this account already created a user
            if(userFromDB == null){
                User userResponse = userService.createUser(user);
                logger.info("New user created by  account ID: {}", account.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
            } else{
                logger.warn("Attempt to createUser with an existing account: {} already owns a profile", account.getUsername());
                return ResponseEntity.ok("error: This account already owns an user profile");
            }
        }
        else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserPartial(@PathVariable Long id, @RequestBody Map<String, Object> updates, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            HttpSession session = request.getSession(false);
            Account account = (Account) session.getAttribute("newAccount");
            if(account.getRole().getRoleId() == 2){
                return ResponseEntity.ok(userService.updateUserPartial(id, updates));
            } else{
                User userFromDB = (User) userService.findByAccountId(account.getAccountId());
                if(userFromDB != null){
                    if(account.getAccountId() == userFromDB.getAccount().getAccountId()){
                        User updatedUser = userService.updateUserPartial(id, updates);
                        return ResponseEntity.ok(updatedUser);
                    } else{
                        return ResponseEntity.ok("error: This account does not own this user profile");
                    }
                } else{
                    return ResponseEntity.ok("error: This account has not created the user profile");
                }
            }
        } else {
            logger.warn("No active session found for request: {}", request.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user, HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if (account.getRole().getRoleId() == 2) {
                //Manager can update any user
                User updatedUser = userService.updateUser(id, user);
                logger.info("Updating user ID: {} by manager: {}", id, account.getUsername());
                return ResponseEntity.ok(updatedUser);
            } else{
                User userFromDB = (User) userService.findByAccountId(account.getAccountId());
                if(userFromDB != null){
                    if(account.getAccountId() == userFromDB.getAccount().getAccountId()){
                        User updatedUser = userService.updateUser(id, user);
                        logger.info("Updating user ID: {} by customer: {}", id, account.getUsername());
                        return ResponseEntity.ok(updatedUser);
                    } else{
                        return ResponseEntity.ok("error: This account does not own this user profile");
                    }
                } else{
                    return ResponseEntity.ok("error: This account has not created the user profile");
                }
            }
        } else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @PutMapping("/myuser")
    public ResponseEntity<?> updateMyUser(@RequestBody User user, HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userFromDB = userService.findByAccountId(account.getAccountId());
            if (userFromDB==null ){
                logger.error("User not found for accountId: {}", account.getAccountId());
                return ResponseEntity.ok("error: User not found !");
            }

            userFromDB.setPhone(user.getPhone());
            userFromDB.setFirstName(user.getFirstName());
            userFromDB.setLastName(user.getLastName());
            userFromDB.setEmail(user.getEmail());
            userFromDB.setCreatedAt(user.getCreatedAt());
            logger.info("Updating user by: {}", account.getUsername());
            User updatedUser = userService.updateUser(userFromDB.getIdUser(),userFromDB);
            return ResponseEntity.ok(updatedUser);
        }
        else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @GetMapping("/myInfo")
    public ResponseEntity<?> getMyUserInfo(HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null){
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            User userFromDB = userService.findByAccountId(account.getAccountId());
            if (userFromDB==null ){
                logger.error("User not found for accountId: {}", account.getAccountId());
                return ResponseEntity.ok("error: User not found !");
            }
            logger.info("User requested my user");
            Optional<User> updatedUser = userService.getMyUserInfo(userFromDB.getIdUser());
            return ResponseEntity.ok(updatedUser);
        }
        else{
            logger.warn("No active session found for request: {}", httpServletRequest.getRequestURI());
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }
}