package com.audioslide.upload.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideoCreatedEvent(String videoObjectKey, UUID videoId) {}
