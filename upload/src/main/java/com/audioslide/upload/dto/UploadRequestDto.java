package com.audioslide.upload.dto;

import com.audioslide.upload.entity.PrivacyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.List;

public record UploadRequestDto(
        @NotBlank String title,
        String description,
        @NotNull PrivacyStatus privacyStatus,
        Integer categoryId,
        List<String> tags,
        Instant publishAt,
        @NotNull Boolean madeForKids
) {
}
