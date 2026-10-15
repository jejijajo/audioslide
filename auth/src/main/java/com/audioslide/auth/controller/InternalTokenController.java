package com.audioslide.auth.controller;

import com.audioslide.auth.config.InternalClientProperties;
import com.audioslide.auth.dto.InternalTokenRequestDto;
import com.audioslide.auth.dto.InternalTokenResponseDto;
import com.audioslide.auth.exception.InternalTokenException;
import com.audioslide.auth.service.InternalTokenService;
import com.audioslide.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class InternalTokenController {


    private final InternalTokenService internalTokenService;

    @PostMapping("/internal/token")
    public InternalTokenResponseDto issueServiceToken(@RequestBody InternalTokenRequestDto internalTokenRequestDto) throws Exception {
        return internalTokenService.issueServiceToken(internalTokenRequestDto);
    }
}
