package com.audioslide.auth.dto;

import org.springframework.web.bind.annotation.RequestHeader;

public record InternalTokenRequestDto(String clientId, String secret) {}
