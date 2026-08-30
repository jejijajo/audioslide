package com.audioslide.video.event;

import java.util.UUID;

public record VideoCreatedEvent(String videoObjectKey, UUID videoId) {}
