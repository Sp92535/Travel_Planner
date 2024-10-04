package com.SP.proDec.user.service.impl;

import com.SP.proDec.user.service.VerificationLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationLinkServiceImpl implements VerificationLinkService {

    private static final String TOKEN_PREFIX = "travel:";
    private static final long EXPIRATION_TIME = 3; // in minutes

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public String makeToken(String userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, userId, EXPIRATION_TIME, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public String validateToken(String token) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }
}
