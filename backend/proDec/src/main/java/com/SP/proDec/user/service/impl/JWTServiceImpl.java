package com.SP.proDec.user.service.impl;

import com.SP.proDec.user.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {

    private static final long EXPIRY_PERIOD = 1000*60;
    private static final String SECRET_KEY = "STAR_PLATINUM_THE_WORLD_ORA_ORA_ORA_ORA_ORA_ORA_ORA_ORA_ORA";


    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Override
    public String generateToken(String email){

        Map<String, Object> claims = new HashMap<>();

        Date curr = new Date(System.currentTimeMillis());
        Date exp = new Date(System.currentTimeMillis() + EXPIRY_PERIOD);


        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(curr)
                .expiration(exp)
                .and()
                .signWith(this.getKey())
                .compact();
    }

    @Override
    public String extractUserName(String token){
        return extractClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && ! isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

}
