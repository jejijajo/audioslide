package com.audioslide.auth.config;

import com.audioslide.auth.entity.GoogleToken;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.service.GoogleTokenService;
import com.audioslide.auth.service.JwtService;
import com.audioslide.auth.service.RefreshTokenService;
import com.audioslide.auth.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtIssuingSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final GoogleTokenService googleTokenService;

    private static final String YOUTUBE_UPLOAD_SCOPE = "https://www.googleapis.com/auth/youtube.upload";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");

        User user = userService.findOrCreateByEmail(email, name, googleId);

        // google token

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        OAuth2AccessToken googleAccessToken = authorizedClient.getAccessToken();
        OAuth2RefreshToken googleRefreshToken = authorizedClient.getRefreshToken();
        String refreshTokenValue = googleRefreshToken != null ? googleRefreshToken.getTokenValue() : null;

        boolean hasUploadScope = googleAccessToken.getScopes().contains(YOUTUBE_UPLOAD_SCOPE);

        try {
            String channelId = googleTokenService.fetchChannelId(googleAccessToken.getTokenValue());
            googleTokenService.addToken(user, googleAccessToken, refreshTokenValue, googleAccessToken.getExpiresAt(), channelId);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to store Google token");
            return;
        }

        // jwt

        String accessToken = jwtService.createToken(user.getId(), user.getEmail(), user.getRoles());
        String refreshToken = refreshTokenService.issue(user);
        Claims claims = jwtService.validateAndParse(accessToken);

        res.setContentType("application/json");
        res.getWriter().write("""
        {"accessToken":"%s","refreshToken":"%s","id":"%s","youtubeUploadGranted":%s}
        """.formatted(accessToken, refreshToken, claims.getSubject(), hasUploadScope));
    }
}