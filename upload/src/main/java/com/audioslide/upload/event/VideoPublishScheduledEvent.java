package com.audioslide.upload.event;

import java.time.Instant;

public record VideoPublishScheduledEvent(Long userId, Instant date) {
}
