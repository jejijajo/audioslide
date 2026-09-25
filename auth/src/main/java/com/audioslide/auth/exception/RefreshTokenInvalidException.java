package com.audioslide.auth.exception;

public class RefreshTokenInvalidException extends RuntimeException {
    public RefreshTokenInvalidException(String message, Throwable cause) {
        super(message, cause);
    }
}
