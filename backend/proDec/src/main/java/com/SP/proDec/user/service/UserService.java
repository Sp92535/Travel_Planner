package com.SP.proDec.user.service;

import com.SP.proDec.user.model.User;

public interface UserService {
    User registerUser(User user);
    String verifyUser(String email, String password);
    User findByEmail(String email);
    String enableUser(String token);
}
