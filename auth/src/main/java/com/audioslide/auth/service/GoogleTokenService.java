package com.audioslide.auth.service;

import com.audioslide.auth.dto.GoogleRefreshApiRequest;
import com.audioslide.auth.dto.GoogleTokenApiResponse;
import com.audioslide.auth.dto.YoutubeChannelListResponse;
import com.audioslide.auth.dto.YoutubeTokenResponseDto;
import com.audioslide.auth.entity.GoogleToken;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.repository.GoogleTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoogleTokenService {

    private final GoogleTokenRepository googleTokenRepository;
    private final CryptoService cryptoService;
    private final RestClient restClient = RestClient.create();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String YOUTUBE_UPLOAD_SCOPE = "https://www.googleapis.com/auth/youtube.upload";


    public GoogleToken addToken(User user, OAuth2AccessToken accessToken, String refreshToken, Instant expiresAt, String channelId) {

        GoogleToken googleToken = googleTokenRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    GoogleToken t = new GoogleToken();
                    t.setUser(user);
                    return t;
                });

        googleToken.setExpiresAt(expiresAt);
        googleToken.setUser(user);
        String scopeString = String.join(" ", accessToken.getScopes());
        googleToken.setScope(scopeString);
        googleToken.setEncryptedAccessToken(cryptoService.encrypt(accessToken.getTokenValue()));
        googleToken.setChannelId(channelId);

        if (refreshToken != null) {
            googleToken.setEncryptedRefreshToken(cryptoService.encrypt(refreshToken));
        }

        return googleTokenRepository.save(googleToken);
    }

    public GoogleToken getValidAccessToken(Long userId) {
        GoogleToken token = googleTokenRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalStateException("No Google token for user " + userId));

        if (!hasUploadScope(token)) {
            throw new RuntimeException(
                    "User " + userId + " has not granted youtube.upload scope");
        }

        if (token.getExpiresAt().isAfter(Instant.now().plusSeconds(60))) {
            return token; // if alive
        }

        return refresh(token);
    }

    private GoogleToken refresh(GoogleToken token) {
        if (token.getEncryptedRefreshToken() == null) {
            throw new IllegalStateException("No refresh token available");
        }

        String refreshToken = cryptoService.decrypt(token.getEncryptedRefreshToken());
        GoogleRefreshApiRequest request = GoogleRefreshApiRequest.of(clientId, clientSecret, refreshToken);

        try {
            GoogleTokenApiResponse response = restClient.post()
                    .uri(TOKEN_URI)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(toFormBody(request))
                    .retrieve()
                    .body(GoogleTokenApiResponse.class);

            token.setEncryptedAccessToken(cryptoService.encrypt(response.access_token()));
            token.setExpiresAt(Instant.now().plusSeconds(response.expires_in()));
            return googleTokenRepository.save(token);
        } catch (HttpClientErrorException e) {
            if (isInvalidGrant(e)) {
                token.setEncryptedRefreshToken(null);
                googleTokenRepository.save(token);
                throw new IllegalStateException("Refresh token revoked, user must re-consent", e);
            }
            throw e;
        }
    }

    private MultiValueMap<String, String> toFormBody(GoogleRefreshApiRequest req) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", req.client_id());
        form.add("client_secret", req.client_secret());
        form.add("refresh_token", req.refresh_token());
        form.add("grant_type", req.grant_type());
        return form;
    }

    private boolean isInvalidGrant(HttpClientErrorException e) {
        return e.getResponseBodyAsString().contains("invalid_grant");
    }

    private boolean hasUploadScope(GoogleToken token) {
        return token.getScope() != null
                && List.of(token.getScope().split(" ")).contains(YOUTUBE_UPLOAD_SCOPE);
    }

    public YoutubeTokenResponseDto getValidAccessTokenDto(Long userId) {
        GoogleToken token = getValidAccessToken(userId);
        return new YoutubeTokenResponseDto(
                cryptoService.decrypt(token.getEncryptedAccessToken()),
                token.getChannelId(),
                token.getExpiresAt()
        );
    }

    public String fetchChannelId(String accessToken) {
        var response = restClient.get()
                .uri("https://www.googleapis.com/youtube/v3/channels?part=id&mine=true")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(YoutubeChannelListResponse.class);

        if (response == null || response.items().isEmpty()) {
            throw new IllegalStateException("No YouTube channel found");
        }
        return response.items().get(0).id();
    }
}
