package com.audioslide.auth.controller;

import com.audioslide.auth.dto.RefreshRequestDto;
import com.audioslide.auth.dto.TokenResponseDto;
import com.audioslide.auth.entity.RefreshToken;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.service.JwtService;
import com.audioslide.auth.service.RefreshTokenService;
import com.nimbusds.oauth2.sdk.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshController {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public TokenResponseDto refresh(@RequestBody RefreshRequestDto request) {
        RefreshToken stored = refreshTokenService.validate(request.refreshToken());
        User user = stored.getUser();

        refreshTokenService.revoke(stored);
        String newRefreshToken = refreshTokenService.issue(user);

        String newAccessToken = jwtService.createToken(user.getId(), user.getEmail(), user.getRoles());

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}
