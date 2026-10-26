package com.audioslide.upload.dto;

import com.audioslide.upload.entity.PrivacyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record UploadRequestDto(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 5000) String description,
        @NotNull PrivacyStatus privacyStatus,
        Integer categoryId,
        List<@Size(max = 500) String> tags,
        Instant publishAt,
        @NotNull Boolean madeForKids
) {}

