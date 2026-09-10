package com.audioslide.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {

    @Value("${jwt.private-key}")
    private String privateKeyPem;

    @Value("${jwt.public-key}")
    private String publicKeyPem;

    @Value("${jwt.access-token-ttl}")
    private long validityInMilliseconds;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        String privatePem = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("\\n", "")
                .replaceAll("\\s", "");
        byte[] privateKeyBytes = Base64.getDecoder().decode(privatePem);
        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateSpec);

        String publicPem = publicKeyPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\\n", "")
                .replaceAll("\\s", "");
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicPem);
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKeyBytes);
        publicKey = KeyFactory.getInstance("RSA").generatePublic(publicSpec);
    }

    public String createToken(Long userId, String email, Set<String> roles) {
        Date now = new Date();

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("roles", roles)
                .issuer("auth-service")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validityInMilliseconds))
                .signWith(privateKey)
                .compact();
    }

    public String createServiceToken(String serviceName, Set<String> scopes) {
        Date now = new Date();

        return Jwts.builder()
                .subject(serviceName)
                .claim("type", "service")
                .claim("scopes", scopes)
                .issuer("auth-service")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 5 * 60 * 1000))
                .signWith(privateKey)
                .compact();
    }

    public Claims validateAndParse(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}