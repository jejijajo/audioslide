package com.audioslide.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-ttl}")
    private long validityInMilliseconds;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(secretKey.getBytes(StandardCharsets.UTF_8));
            signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public String createToken(Long userId, String email, Set<String> roles) {

        Date now = new Date();

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .issuer("auth-service")
                .issuedAt(now)
                .expiration(new Date(now.getTime()+validityInMilliseconds))
                .signWith(signingKey)
                .compact();
    }

    public Claims validateAndParse(String token) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
