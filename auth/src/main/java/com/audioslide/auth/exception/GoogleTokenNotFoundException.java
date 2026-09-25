package com.audioslide.auth.exception;

public class GoogleTokenNotFoundException extends RuntimeException {
    public GoogleTokenNotFoundException(String message) {
        super(message);
    }
}
