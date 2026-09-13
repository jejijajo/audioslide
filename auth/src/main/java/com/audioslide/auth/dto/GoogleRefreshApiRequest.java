package com.audioslide.auth.dto;

public record GoogleRefreshApiRequest(
        String client_id,
        String client_secret,
        String refresh_token,
        String grant_type
) {
    public static GoogleRefreshApiRequest of(String clientId, String clientSecret, String refreshToken) {
        return new GoogleRefreshApiRequest(clientId, clientSecret, refreshToken, "refresh_token");
    }
}