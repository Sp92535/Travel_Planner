package com.SP.proDec.user.controller;

import com.SP.proDec.user.dto.LoginRequestDto;
import com.SP.proDec.user.model.User;
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
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        String res = userService.verifyUser(loginRequestDto.email(), loginRequestDto.password());
        return new ResponseEntity<>(res, HttpStatus.OK) ;
    }

    @GetMapping("/verify")
    public ResponseEntity<String> enableUser(@RequestParam("token") String token){
        String res = userService.enableUser(token);
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

}
