package com.SP.proDec.user.service.impl;

import com.SP.proDec.user.model.User;
import com.SP.proDec.user.repository.UserRepository;
import com.SP.proDec.user.service.EmailService;
import com.SP.proDec.user.service.JWTService;
import com.SP.proDec.user.service.UserService;
import com.SP.proDec.user.service.VerificationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationLinkService verificationLinkService;

    @Override
    public User registerUser(User user) {

        user.setPassword(encoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String userId = savedUser.getId().toString();
        String token = verificationLinkService.makeToken(userId);

        String verificationLink = "http://localhost:6969/auth/verify?token="+token;

        String subject = "Please verify your email address";
        String text = "Click the link to verify your account: " + verificationLink;

        emailService.sendSimpleEmail(user.getEmail(), subject, text);

        return savedUser;
    }

    @Override
    public String verifyUser(String email, String password) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(email,password));

        return authentication.isAuthenticated() ? jwtService.generateToken(email):"";
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
    }

    @Override
    public String enableUser(String token){
        String userId = verificationLinkService.validateToken(token);
        if(userId==null){
            return "VERIFICATION LINK IS INVALID OR EXPIRED";
        }
        Integer uid = Integer.valueOf(userId);
        User user = userRepository.findById(uid).orElse(null);
        if(user!=null){
            user.setEnabled(true);
            userRepository.save(user);
            return "ACCOUNT ENABLE YOU CAN LOGIN NOW";
        }
        return "USER NOT FOUND";
    }

}
