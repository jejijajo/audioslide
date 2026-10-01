package com.audioslide.auth.exception;

public class GoogleReconsentRequiredException extends RuntimeException {
    public GoogleReconsentRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
