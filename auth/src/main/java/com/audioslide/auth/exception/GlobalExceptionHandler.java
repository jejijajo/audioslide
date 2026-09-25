package com.audioslide.auth.exception;

import com.audioslide.auth.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GoogleTokenNotFoundException.class)
    public ResponseEntity<ErrorDto> handleGoogleTokenNotFound(GoogleTokenNotFoundException e) {
        log.warn("Google token not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(YoutubeScopeMissingException.class)
    public ResponseEntity<ErrorDto> handleScopeMissing(YoutubeScopeMissingException e) {
        log.warn("Youtube upload scope missing: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(GoogleReconsentRequiredException.class)
    public ResponseEntity<ErrorDto> handleReconsentRequired(GoogleReconsentRequiredException e) {
        log.warn("Google re-consent required", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(GoogleTokenException.class)
    public ResponseEntity<ErrorDto> handleGoogleTokenException(GoogleTokenException e) {
        log.error("Google token operation failed", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<ErrorDto> handleRefreshTokenInvalid(RefreshTokenInvalidException e) {
        log.warn("Invalid refresh token: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFound(UserNotFoundException e) {
        log.warn("User not found: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(CryptoOperationException.class)
    public ResponseEntity<ErrorDto> handleCryptoException(CryptoOperationException e) {
        log.error("Crypto operation failed", e);
        // сообщение клиенту не отдаём — может утечь контекст
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Internal error"));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorDto> handleAuthException(AuthException e) {
        log.error("Auth error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Internal error"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpected(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Internal error"));
    }

}
