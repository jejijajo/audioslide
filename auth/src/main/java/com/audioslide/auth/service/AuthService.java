package com.audioslide.auth.service;

import com.audioslide.auth.dto.RefreshRequestDto;
import com.audioslide.auth.dto.TokenResponseDto;
import com.audioslide.auth.entity.RefreshToken;
import com.audioslide.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    @Transactional
    public TokenResponseDto refresh(RefreshRequestDto request) {
        RefreshToken stored = refreshTokenService.validate(request.refreshToken());
        User user = stored.getUser();

        refreshTokenService.revoke(stored);
        String newRefreshToken = refreshTokenService.issue(user);

        String newAccessToken = jwtService.createToken(user.getId(), user.getEmail(), user.getRoles());

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }
}
