package com.audioslide.youtube.dto;

import java.time.Instant;

public record YoutubeTokenResponseDto(String accessToken, String channelId, Instant expiresAt) {
}
