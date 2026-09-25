package com.audioslide.auth.service;

import com.audioslide.auth.exception.CryptoOperationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CryptoService {

    private final TextEncryptor encryptor;

    public CryptoService(@Value("${security.encryption.key}") String key,
                         @Value("${security.encryption.salt}") String salt) {
        this.encryptor = Encryptors.text(key, salt);
    }

    public String encrypt(String plainText) {

        log.info("Google token encrypted");
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String encrypted) {
        String decrypted;
        try {
            decrypted = encryptor.decrypt(encrypted);
            log.info("Google token decrypted");
        } catch (Exception e) {
            throw new CryptoOperationException("Failed to decrypt stored value", e);
        }

        return decrypted;
    }
}
