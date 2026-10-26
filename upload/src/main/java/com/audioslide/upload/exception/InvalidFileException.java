package com.audioslide.upload.exception;

public class InvalidFileException extends RuntimeException {
    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
