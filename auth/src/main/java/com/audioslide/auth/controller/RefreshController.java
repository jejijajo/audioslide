package com.audioslide.auth.controller;

import com.audioslide.auth.dto.RefreshRequestDto;
import com.audioslide.auth.dto.TokenResponseDto;
import com.audioslide.auth.entity.RefreshToken;
import com.audioslide.auth.entity.User;
import com.audioslide.auth.service.AuthService;
import com.audioslide.auth.service.JwtService;
import com.audioslide.auth.service.RefreshTokenService;
import com.nimbusds.oauth2.sdk.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RefreshController {

    private final AuthService authService;

    @PostMapping("/refresh")
    public TokenResponseDto refresh(@RequestBody RefreshRequestDto request) {
        return authService.refresh(request);
    }
}
