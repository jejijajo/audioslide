package com.audioslide.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

@Service
public class CryptoService {

    private final TextEncryptor encryptor;

    public CryptoService(@Value("${security.encryption.key}") String key,
                         @Value("${security.encryption.salt}") String salt) {
        this.encryptor = Encryptors.text(key, salt);
    }

    public String encrypt(String plainText) {
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String encrypted) {
        return encryptor.decrypt(encrypted);
    }
}
