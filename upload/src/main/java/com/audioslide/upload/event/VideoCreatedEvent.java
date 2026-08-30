package com.audioslide.upload.event;

import java.util.UUID;

public record VideoCreatedEvent(String videoObjectKey, UUID videoId) {}
