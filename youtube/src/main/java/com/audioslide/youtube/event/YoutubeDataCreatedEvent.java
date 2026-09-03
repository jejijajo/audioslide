package com.audioslide.youtube.event;


import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record YoutubeDataCreatedEvent(
        UUID id,
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
