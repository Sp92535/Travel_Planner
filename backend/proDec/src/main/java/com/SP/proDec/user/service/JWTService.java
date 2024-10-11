package com.SP.proDec.user.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JWTService {
    public String generateToken(String email);
    public String extractUserName(String token);
    public boolean validateToken(String token, UserDetails userDetails);
}
