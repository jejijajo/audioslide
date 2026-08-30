package com.audioslide.video.event;

import java.util.UUID;

public record FileLinksEvent(String pictureObjectKey, String audioObjectKey, UUID videoId) {
}
