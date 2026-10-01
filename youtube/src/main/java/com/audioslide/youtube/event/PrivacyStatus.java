package com.audioslide.youtube.event;

import lombok.Getter;

@Getter
public enum PrivacyStatus {

    PRIVATE("private"), UNLISTED("unlisted"), PUBLIC("public");

    private final String value;

    PrivacyStatus(String value) {
        this.value = value;
    }
}
