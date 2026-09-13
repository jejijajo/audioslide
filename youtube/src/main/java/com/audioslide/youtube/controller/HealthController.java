package com.audioslide.youtube.controller;

import com.audioslide.youtube.AuthServiceClient;
import com.audioslide.youtube.dto.YoutubeTokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final AuthServiceClient authServiceClient;

    @GetMapping("/health")
    public String health() {return "Youtube service is running...";}

    @GetMapping("/videos/test-auth/{userId}")
    public YoutubeTokenResponseDto testAuth(@PathVariable Long userId) {
        return authServiceClient.getYoutubeToken(userId);
    }
}
