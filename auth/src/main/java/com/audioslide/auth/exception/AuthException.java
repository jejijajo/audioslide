package com.audioslide.auth.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
