package com.audioslide.auth.dto;

public record GoogleTokenApiResponse(
        String access_token,
        Integer expires_in,
        String scope,
        String token_type
) {}