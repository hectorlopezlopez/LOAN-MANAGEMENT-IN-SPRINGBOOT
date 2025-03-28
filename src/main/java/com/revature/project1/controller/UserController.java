package com.revature.project1.controller;

import com.revature.project1.Entities.Account;
import com.revature.project1.Entities.User;
import com.revature.project1.service.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
                return ResponseEntity.ok(users);
            }
            else {
                return ResponseEntity.ok("error: You have no permission to take this action!");
            }
        }
        else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id , HttpServletRequest httpServletRequest){
        if (httpServletRequest.getSession(false) != null) {
            HttpSession httpSession = httpServletRequest.getSession(false);
            Account account = (Account) httpSession.getAttribute("newAccount");
            if (account.getRole().getRoleId() == 2) {
                Optional<User> user = userService.getUserById(id);
                if(user.isPresent()){
                    return ResponseEntity.ok(user.orElseThrow());
                }else{
                    return ResponseEntity.ok("error: No user found!");
                }
            } else {
                return ResponseEntity.ok("error: You have no permission to take this action!");

            }
        }else{
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
                return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
            } else{
                return ResponseEntity.ok("error: This account already owns an user profile");
            }
        }
        else{
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
                return ResponseEntity.ok(updatedUser);
            } else{
                User userFromDB = (User) userService.findByAccountId(account.getAccountId());
                if(userFromDB != null){
                    if(account.getAccountId() == userFromDB.getAccount().getAccountId()){
                        User updatedUser = userService.updateUser(id, user);
                        return ResponseEntity.ok(updatedUser);
                    } else{
                        return ResponseEntity.ok("error: This account does not own this user profile");
                    }
                } else{
                    return ResponseEntity.ok("error: This account has not created the user profile");
                }
            }
        } else{
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
                return ResponseEntity.ok("error: User not found !");
            }

            userFromDB.setPhone(user.getPhone());
            userFromDB.setFirstName(user.getFirstName());
            userFromDB.setLastName(user.getLastName());
            userFromDB.setEmail(user.getEmail());
            userFromDB.setCreatedAt(user.getCreatedAt());
            User updatedUser = userService.updateUser(userFromDB.getIdUser(),userFromDB);
            return ResponseEntity.ok(updatedUser);
        }
        else{
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
                return ResponseEntity.ok("error: User not found !");
            }
            Optional<User> updatedUser = userService.getMyUserInfo(userFromDB.getIdUser());
            return ResponseEntity.ok(updatedUser);
        }
        else{
            return ResponseEntity.ok("error: Invalid action (no session is in progress)!");
        }
    }
}