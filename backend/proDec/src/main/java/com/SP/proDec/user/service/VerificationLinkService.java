package com.SP.proDec.user.service;

public interface VerificationLinkService {
    public String makeToken(String id);
    public String validateToken(String token);
}
