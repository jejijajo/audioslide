package com.audioslide.youtube.exception;

public abstract class YoutubeProcessingException extends RuntimeException {
    public YoutubeProcessingException(String message, Throwable cause) {
        super(message,cause);
    }
}
