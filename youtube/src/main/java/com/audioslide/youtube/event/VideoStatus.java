package com.audioslide.youtube.event;

public enum VideoStatus {

    READY("ready"), UPLOADED("uploaded"), GENERATING("generating"), PUBLISHED("published");

    private final String value;

    VideoStatus(String value) {
        this.value = value;
    }
}
