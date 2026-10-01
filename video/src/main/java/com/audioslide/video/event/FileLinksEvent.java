package com.audioslide.video.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FileLinksEvent(String pictureObjectKey, String audioObjectKey, UUID videoId) {
}
