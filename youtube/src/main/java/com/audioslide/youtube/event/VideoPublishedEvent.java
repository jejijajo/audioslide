package com.audioslide.youtube.event;

import java.util.UUID;

public record VideoPublishedEvent(
        UUID id,
        String YoutubeVideoLink) {
}
