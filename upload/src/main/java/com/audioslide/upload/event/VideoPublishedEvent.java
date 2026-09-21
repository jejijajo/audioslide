package com.audioslide.upload.event;

import java.util.UUID;

public record VideoPublishedEvent(
        UUID id,
        String YoutubeVideoLink) {
}
