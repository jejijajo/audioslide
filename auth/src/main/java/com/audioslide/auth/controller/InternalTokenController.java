package com.audioslide.auth.controller;

import com.audioslide.auth.config.InternalClientProperties;
import com.audioslide.auth.dto.InternalTokenRequestDto;
import com.audioslide.auth.dto.InternalTokenResponseDto;
import com.audioslide.auth.exception.InternalTokenException;
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


    private final JwtService jwtService;
    private final InternalClientProperties clients;

    @PostMapping("/internal/token")
    public InternalTokenResponseDto issueServiceToken(@RequestBody InternalTokenRequestDto internalTokenRequestDto) throws Exception {
        var config = clients.getClients().get(internalTokenRequestDto.clientId());
        if (config == null || !config.getSecret().equals(internalTokenRequestDto.secret())) {
            throw new InternalTokenException("internal secret invalid");
        }

        Set<String> scopes = Set.of(config.getScopes().split(","));
        String token = jwtService.createServiceToken(internalTokenRequestDto.clientId(), scopes);

        return new InternalTokenResponseDto(token, 300);
    }
}
