package com.audioslide.auth.controller;

import com.audioslide.auth.dto.YoutubeTokenResponseDto;
import com.audioslide.auth.service.GoogleTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalYoutubeTokenController {

    private final GoogleTokenService googleTokenService;

    @GetMapping("/{userId}/youtube-token")
    public YoutubeTokenResponseDto getYoutubeToken(@PathVariable Long userId) {
        return googleTokenService.getValidAccessTokenDto(userId);
    }
}
