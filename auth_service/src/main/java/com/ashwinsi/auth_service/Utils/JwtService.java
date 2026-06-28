package com.ashwinsi.auth_service.Utils;

import com.ashwinsi.auth_service.DTO.SellerJwtData;
import com.ashwinsi.auth_service.DTO.UserJwtData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

    private final String SECRET_STRING = "your_super_secret_key_that_is_long_enough_for_sha_256";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes(StandardCharsets.UTF_8));
    private final long EXPIRATION_TIME_MS = 3600000; // 1 hour

    public String generateUserToken(UserJwtData userJwtData) {
        Claims claims = generateUserClaims(userJwtData);
        return Jwts.builder()
                .claims(claims)
                .subject(userJwtData.getEmail()) // Sets standard 'sub' claim
                .signWith(SECRET_KEY)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .issuedAt(new Date(System.currentTimeMillis()))
                .compact();
    }

    public String generateSellerToken(SellerJwtData sellerJwtData) {
        Claims claims = generateSellerClaims(sellerJwtData);
        return Jwts.builder()
                .claims(claims)
                .subject(sellerJwtData.getEmail()) // Sets standard 'sub' claim
                .signWith(SECRET_KEY)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .issuedAt(new Date(System.currentTimeMillis()))
                .compact();
    }


    private Claims generateUserClaims(UserJwtData userJwtData) {
        return Jwts.claims()
                .add("id", userJwtData.getId())
                .add("email", userJwtData.getEmail())
                .build();
    }

    private Claims generateSellerClaims(SellerJwtData sellerJwtData) {
        return Jwts.claims()
                .add("id", sellerJwtData.getId())
                .add("email", sellerJwtData.getEmail())
                .add("isAdmin", sellerJwtData.getIsAdmin())
                .build();
    }
}