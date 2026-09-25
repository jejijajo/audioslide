package com.audioslide.auth.exception;

public class GoogleTokenException extends RuntimeException {
    public GoogleTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
