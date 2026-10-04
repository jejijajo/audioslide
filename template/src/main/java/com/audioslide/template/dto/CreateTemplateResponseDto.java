package com.audioslide.template.dto;

import java.util.List;

public record CreateTemplateResponseDto(
        Long templateId,
        String templateName,
        String title,
        String description,
        Integer categoryId,
        List<String> tags,
        Boolean madeForKids) {
}
