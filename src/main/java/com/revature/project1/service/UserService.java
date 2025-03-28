package com.revature.project1.service;

import com.revature.project1.Entities.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService {

    List<User> getUsers();
    Optional<User> getMyUserInfo(Long id);
    User createUser(User user);
    Optional<User> getUserById(Long id);
    User updateUser(Long id,User user);
    User updateUserPartial(Long id, Map<String, Object> updates);


}
