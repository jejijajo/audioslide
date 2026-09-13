package com.audioslide.auth.dto;

public record InternalTokenResponseDto(String accessToken, Integer expiresAt) {
}
