package com.audioslide.upload.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoPublishedEvent(
        UUID id,
        String YoutubeVideoLink) {
}
