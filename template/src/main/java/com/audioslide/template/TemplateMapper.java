package com.audioslide.template;

import com.audioslide.template.dto.CreateTemplateRequestDto;
import com.audioslide.template.dto.CreateTemplateResponseDto;
import com.audioslide.template.dto.ReadAllTemplatesResponseDto;
import com.audioslide.template.dto.ReadTemplateResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TemplateMapper {

    public static ReadTemplateResponseDto fromTemplateEntityToReadTemplateResponseDto(TemplateEntity templateEntity) {
        return new ReadTemplateResponseDto(
                templateEntity.getId(),
                templateEntity.getTemplateName(),
                templateEntity.getTitle(),
                templateEntity.getDescription(),
                templateEntity.getCategoryId(),
                templateEntity.getTags(),
                templateEntity.getMadeForKids()
        );
    }

    public static CreateTemplateResponseDto fromTemplateEntityToCreateTemplateResponseDto(TemplateEntity templateEntity) {
        return new CreateTemplateResponseDto(
                templateEntity.getId(),
                templateEntity.getTemplateName(),
                templateEntity.getTitle(),
                templateEntity.getDescription(),
                templateEntity.getCategoryId(),
                templateEntity.getTags(),
                templateEntity.getMadeForKids()
        );
    }

    public static ReadAllTemplatesResponseDto fromTemplateEntityListToReadAllTemplatesResponseDto(List<TemplateEntity> list) {
        List<ReadTemplateResponseDto> dtoList = new ArrayList<>();
        for(TemplateEntity templateEntity: list) {
            dtoList.add(fromTemplateEntityToReadTemplateResponseDto(templateEntity));
        }
        return new ReadAllTemplatesResponseDto(dtoList);
    }

    public static TemplateEntity fromCreateTemplateRequestDtoToTemplateEntity(CreateTemplateRequestDto dto, Long userId) {
        TemplateEntity templateEntity = new TemplateEntity();

        templateEntity.setTemplateName(dto.templateName());
        templateEntity.setUserId(userId);
        templateEntity.setTitle(dto.title());
        templateEntity.setDescription(dto.description());
        templateEntity.setCategoryId(dto.categoryId());
        templateEntity.setTags(dto.tags());
        templateEntity.setMadeForKids(dto.madeForKids());

        return templateEntity;
    }
}
