package com.audioslide.auth.service;

import com.audioslide.auth.config.InternalClientProperties;
import com.audioslide.auth.dto.InternalTokenRequestDto;
import com.audioslide.auth.dto.InternalTokenResponseDto;
import com.audioslide.auth.exception.InternalTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class InternalTokenService {

    private final JwtService jwtService;
    private final InternalClientProperties clients;

    public InternalTokenResponseDto issueServiceToken(InternalTokenRequestDto internalTokenRequestDto) throws Exception {
        var config = clients.getClients().get(internalTokenRequestDto.clientId());
        if (config == null || !config.getSecret().equals(internalTokenRequestDto.secret())) {
            throw new InternalTokenException("internal secret invalid");
        }

        Set<String> scopes = Set.of(config.getScopes().split(","));
        String token = jwtService.createServiceToken(internalTokenRequestDto.clientId(), scopes);

        return new InternalTokenResponseDto(token, 300);
    }
}
