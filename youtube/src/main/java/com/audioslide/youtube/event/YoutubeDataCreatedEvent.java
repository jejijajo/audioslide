package com.audioslide.youtube.event;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@JsonIgnoreProperties(ignoreUnknown = true)
public record YoutubeDataCreatedEvent(
        UUID id,
        Long userId,
        String title,
        String description,
        PrivacyStatus privacyStatus,
        Integer categoryId,
        List<String> tags,
        Instant createdAt,
        Instant updatedAt,
        Instant publishAt,
        Boolean madeForKids,
        VideoStatus videoStatus,
        String thumbnailS3Key,
        String videoS3Key
) {
}
