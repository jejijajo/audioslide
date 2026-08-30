package com.audioslide.upload.event;

import com.audioslide.upload.entity.PrivacyStatus;
import com.audioslide.upload.entity.VideoStatus;

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
