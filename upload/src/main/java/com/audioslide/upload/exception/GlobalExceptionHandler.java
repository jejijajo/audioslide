package com.audioslide.upload.exception;


import com.audioslide.upload.dto.ErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleVideoNotFound(VideoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleStorageException(StorageException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorDto(e.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorDto> handleMissingPart(MissingServletRequestPartException e) {
        log.warn("Missing request part: {}", e.getRequestPartName());
        return ResponseEntity.badRequest()
                .body(new ErrorDto("Required part is missing: " + e.getRequestPartName()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorDto> handleInvalidFileException(InvalidFileException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(e.getMessage()));
    }
}