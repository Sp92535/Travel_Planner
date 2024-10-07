package com.SP.proDec.user.service;

import com.SP.proDec.user.dto.RegisterRequestDto;

public interface UserService {
    String registerUser(RegisterRequestDto registerRequestDto);
    String loginUser(String email, String password);
    String enableUser(String token);
    String sendResetPasswordLink(String email);
    String resetPassword(String token,String pass);
}
