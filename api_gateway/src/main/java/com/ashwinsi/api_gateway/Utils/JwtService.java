package com.ashwinsi.api_gateway.Utils;

import com.ashwinsi.api_gateway.DTO.SellerJwtData;
import com.ashwinsi.api_gateway.DTO.UserJwtData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
public class JwtService {
    private final String SECRET_STRING = "your_super_secret_key_that_is_long_enough_for_sha_256";
    private final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public UserJwtData parseUserToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();


            Long id = claims.get("id", Long.class);
            String email = claims.get("email", String.class);

            return new UserJwtData(id, email);
        } catch (JwtException | IllegalArgumentException e) {

            return null;
        }
    }

    public SellerJwtData parseSellerToken(String jwtToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(jwtToken)
                    .getPayload();


            Long id = claims.get("id", Long.class);
            String email = claims.get("email", String.class);
            Boolean isAdmin = claims.get("isAdmin", Boolean.class);

            return new SellerJwtData(id, email, isAdmin);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isValidToken(String jwtToken) {
        try {
            Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(jwtToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
