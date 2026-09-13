package com.audioslide.youtube;

import com.audioslide.youtube.dto.InternalTokenResponseDto;
import com.audioslide.youtube.dto.YoutubeTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceClient {

    private final RestClient restClient = RestClient.create();

    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${internal.client-id}")
    private String clientId;

    @Value("${internal.secret}")
    private String secret;

    private String cachedToken;
    private Instant tokenExpiresAt;

    private String getInternalToken() {
        if (cachedToken != null && tokenExpiresAt.isAfter(Instant.now().plusSeconds(10))) {
            return cachedToken;
        }
        var response = restClient.post()
                .uri(authServiceUrl + "/internal/token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("clientId", clientId, "secret", secret))
                .retrieve()
                .body(InternalTokenResponseDto.class);

        cachedToken = response.accessToken();
        tokenExpiresAt = Instant.now().plusSeconds(response.expiresAt());
        return cachedToken;
    }

    public YoutubeTokenResponseDto getYoutubeToken(Long userId) {
        return restClient.get()
                .uri(authServiceUrl + "/internal/users/{userId}/youtube-token", userId)
                .header("Authorization", "Bearer " + getInternalToken())
                .retrieve()
                .body(YoutubeTokenResponseDto.class);
    }
}
