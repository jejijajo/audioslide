package com.audioslide.youtube.controller;

import com.audioslide.youtube.AuthServiceClient;
import com.audioslide.youtube.dto.YoutubeTokenResponseDto;
import com.audioslide.youtube.event.PrivacyStatus;
import com.audioslide.youtube.event.VideoStatus;
import com.audioslide.youtube.event.YoutubeDataCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final AuthServiceClient authServiceClient;

    @GetMapping("/health")
    public String health() {return "Youtube service is running...";}

}
