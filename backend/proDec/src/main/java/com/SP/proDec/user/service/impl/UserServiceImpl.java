package com.SP.proDec.user.service.impl;

import com.SP.proDec.user.dto.RegisterRequestDto;
import com.SP.proDec.user.exception.InvalidTokenException;
import com.SP.proDec.user.exception.UserAlreadyExistsException;
import com.SP.proDec.user.exception.UserNotFoundException;
import com.SP.proDec.user.model.User;
import com.SP.proDec.user.repository.UserRepository;
import com.SP.proDec.user.service.EmailService;
import com.SP.proDec.user.service.JWTService;
import com.SP.proDec.user.service.UserService;
import com.SP.proDec.user.service.VerificationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    public String registerUser(RegisterRequestDto registerRequestDto) {

        User user = new User();

        user.setFirstName(registerRequestDto.firstName());
        user.setLastName(registerRequestDto.lastName());
        user.setEmail(registerRequestDto.email());
        user.setPassword(encoder.encode(registerRequestDto.password()));

        try {
            User savedUser = userRepository.save(user);

            // Sending mail to verify email address
            String userId = savedUser.getId().toString();
            String token = verificationLinkService.makeToken(userId);

            String verificationLink = "http://localhost:6969/auth/verify?token=" + token;

            String subject = "Please verify your email address";
            String text = "Click the link to verify your account: " + verificationLink;

            emailService.sendSimpleEmail(user.getEmail(), subject, text);

            return "Click on verification link sent to your registered email.";

        } catch (DataIntegrityViolationException ex) {
            throw new UserAlreadyExistsException("User with email " + registerRequestDto.email() + " already exists.");
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during registration: " + e.getMessage());
        }
    }

    @Override
    public String loginUser(String email, String password){
        try {
            // Authenticate user
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));

            // Check if the authentication is successful
            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(email);
            } else {
                throw new BadCredentialsException("Invalid email or password.");
            }

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password.");
        } catch (Exception e) {
            throw new RuntimeException("Error occurred during login: " + e.getMessage());
        }
    }

    @Override
    public String enableUser(String token){
        try {
            // Validate token and get the associated userId
            String userId = verificationLinkService.validateToken(token);
            if (userId == null) {
                throw new InvalidTokenException("Verification link is invalid or expired.");
            }

            // Convert userId to integer
            Integer uid = Integer.valueOf(userId);

            // Fetch the user by ID
            User user = userRepository.findById(uid).orElseThrow(() ->
                    new UserNotFoundException("User not found for the given token.")
            );

            // Enable the user if found
            user.setEnabled(true);
            userRepository.save(user);

            return "Account enabled, you can log in now.";

        } catch (InvalidTokenException | UserNotFoundException ex) {
            throw ex;  // Rethrow the specific exception
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while enabling the user: " + e.getMessage());
        }
    }

    @Override
    public String sendResetPasswordLink(String email) {

        try {
            // Fetch user by email
            User user = userRepository.findByEmail(email).orElseThrow(() ->
                    new UserNotFoundException("User with email " + email + " not found.")
            );

            // Generate reset token
            String userId = user.getId().toString();
            String token = verificationLinkService.makeToken(userId);

            // Prepare reset link
            String resetLink = "http://localhost:6969/auth/reset-password?token=" + token;

            String subject = "Reset Password.";
            String text = "Click on this link to reset your password: " + resetLink;

            // Send reset password email
            emailService.sendSimpleEmail(email, subject, text);

            return "Check your mail for the link to reset your password.";

        } catch (UserNotFoundException ex) {
            throw ex;  // Rethrow the specific exception
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while sending reset password link: " + e.getMessage());
        }
    }

    @Override
    public String resetPassword(String token, String pass) {
        try {
            // Validate the token and retrieve userId
            String userId = verificationLinkService.validateToken(token);
            if (userId == null) {
                throw new InvalidTokenException("Verification link is invalid or expired.");
            }

            // Convert userId to integer
            Integer uid = Integer.valueOf(userId);

            // Find user by ID
            User user = userRepository.findById(uid).orElseThrow(() ->
                    new UserNotFoundException("User not found for the given token.")
            );

            // Reset user's password
            user.setPassword(encoder.encode(pass));
            userRepository.save(user);

            return "Password changed successfully.";

        } catch (InvalidTokenException | UserNotFoundException ex) {
            throw ex;  // Rethrow the specific exception
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while resetting password: " + e.getMessage());
        }
    }


}
