package com.audioslide.youtube.exception;


import com.audioslide.youtube.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleStorageException(StorageException e) {
        log.error("Storage error", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleYoutubeAuthException(YoutubeAuthException e) {
        log.error("Youtube auth error", e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleYoutubeUploadException(YoutubeUploadException e) {
        log.error("Youtube upload error", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ErrorDto(e.getMessage()));
    }
}