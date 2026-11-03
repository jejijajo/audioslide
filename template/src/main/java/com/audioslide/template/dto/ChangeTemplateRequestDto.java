package com.audioslide.template.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ChangeTemplateRequestDto(
        @Size(max = 100) String templateName,
        @Size(max = 100) String title,
        @Size(max = 4500) String description,
        Integer categoryId,
        List<@NotBlank String> tags,
        Boolean madeForKids
) {
    @AssertTrue(message = "tags total length must be <= 500")
    public boolean isTagsLengthValid() {
        return tags == null || tags.stream().mapToInt(String::length).sum() <= 500;
    }
}