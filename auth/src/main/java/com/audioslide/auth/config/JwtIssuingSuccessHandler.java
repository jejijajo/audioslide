package com.audioslide.auth.config;

import com.audioslide.auth.entity.User;
import com.audioslide.auth.service.JwtService;
import com.audioslide.auth.service.RefreshTokenService;
import com.audioslide.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtIssuingSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse res,
                                        Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String googleId = oauthUser.getAttribute("sub");

        User user = userService.findOrCreateByEmail(email, name, googleId);

        String accessToken = jwtService.createToken(user.getId(),user.getEmail(),user.getRoles());
        String refreshToken = refreshTokenService.issue(user);

        res.setContentType("application/json");
        res.getWriter().write("""
            {"accessToken":"%s","refreshToken":"%s"}
            """.formatted(accessToken, refreshToken));
    }
}