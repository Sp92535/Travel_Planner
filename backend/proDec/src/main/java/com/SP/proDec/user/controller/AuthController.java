package com.SP.proDec.user.controller;

import com.SP.proDec.user.dto.EmailDto;
import com.SP.proDec.user.dto.LoginRequestDto;
import com.SP.proDec.user.dto.PasswordDto;
import com.SP.proDec.user.dto.RegisterRequestDto;
import com.SP.proDec.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        String res = userService.registerUser(registerRequestDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);  // Use 201 for resource creation
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        String res = userService.loginUser(loginRequestDto.email(), loginRequestDto.password());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> enableUser(@RequestParam("token") String token) {
        String res = userService.enableUser(token);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sendResetPasswordLink(@RequestBody EmailDto emailDto) {
        String res = userService.sendResetPasswordLink(emailDto.email());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestBody PasswordDto passwordDto) {
        String res = userService.resetPassword(token, passwordDto.password());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
