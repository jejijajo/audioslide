package com.audioslide.auth.service;

import com.audioslide.auth.entity.RefreshToken;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.exception.RefreshTokenInvalidException;
import com.audioslide.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-ttl}")
    private long ttl;

    public String issue(User user) {
        String rawToken = generateRawToken();

        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(hash(rawToken));
        entity.setExpiresAt(Instant.now().plus(ttl, ChronoUnit.MILLIS));
        refreshTokenRepository.save(entity);

        log.info("Saved refresh token entity");

        return rawToken;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public RefreshToken validate(String rawToken) {
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new RefreshTokenInvalidException("Invalid refresh token",null));

        if (stored.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(stored);
            throw new RefreshTokenInvalidException("Refresh token expired",null);
        }

        log.info("refresh token is validated");

        return stored;
    }

    public void revoke(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }
}
