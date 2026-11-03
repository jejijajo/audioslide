package com.audioslide.template;

import com.audioslide.template.dto.*;
import com.audioslide.template.exception.InvalidCategoryException;
import com.audioslide.template.exception.TemplateAlreadyExistsException;
import com.audioslide.template.exception.TemplateLimitExceededException;
import com.audioslide.template.exception.TemplateNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateService {

    @Value("${template.max-per-user}")
    private Long MAX_TEMPLATES;

    private final TemplateRepository templateRepository;

    @Transactional
    public CreateTemplateResponseDto createTemplate(CreateTemplateRequestDto dto, Long userId) {
        if(templateRepository.existsByUserIdAndTemplateName(userId, dto.templateName())) {
            throw new TemplateAlreadyExistsException("Template already exists");
        } else if(templateRepository.countByUserId(userId) > MAX_TEMPLATES) {
            throw new TemplateLimitExceededException("Template limit reached");
        } else if (dto.categoryId() != null && !YoutubeCategories.isValid(dto.categoryId())) {
            throw new InvalidCategoryException("Unknown categoryId: " + dto.categoryId());
        }


        TemplateEntity templateEntity = TemplateMapper.fromCreateTemplateRequestDtoToTemplateEntity(dto, userId);
        TemplateEntity savedTemplateEntity = templateRepository.save(templateEntity);

        log.info("templated is created, id: {}", templateEntity.getId());

        return TemplateMapper.fromTemplateEntityToCreateTemplateResponseDto(savedTemplateEntity);
    }

    @Transactional(readOnly = true)
    public ReadTemplateResponseDto readTemplate(Long templateId, Long userId) {
        TemplateEntity templateEntity = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new TemplateNotFoundException("Failed to find template"));

        log.info("successful get template, id: {}", templateEntity.getId());

        return TemplateMapper.fromTemplateEntityToReadTemplateResponseDto(templateEntity);
    }

    @Transactional(readOnly = true)
    public ReadAllTemplatesResponseDto readAllTemplates(Long userId)  {
        List<TemplateEntity> templates = templateRepository.findAllByUserId(userId)
                .orElseThrow(() -> new TemplateNotFoundException("Failed to find templates"));

        log.info("successful get all templates, userId: {}", userId);

        return TemplateMapper.fromTemplateEntityListToReadAllTemplatesResponseDto(templates);
    }

    @Transactional
    public void deleteTemplate(Long templateId, Long userId)  {
        TemplateEntity templateEntity = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new TemplateNotFoundException("Failed to find template"));

        log.info("successful delete template, id: {}", userId);

        templateRepository.delete(templateEntity);
    }

    public ChangeTemplateResponseDto changeTemplate(Long templateId, Long userId, ChangeTemplateRequestDto dto) {
        TemplateEntity templateEntity = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new TemplateNotFoundException("Failed to find template"));

        if (dto.templateName() != null) templateEntity.setTemplateName(dto.templateName());
        if (dto.title() != null) templateEntity.setTitle(dto.title());
        if (dto.description() != null) templateEntity.setDescription(dto.description());
        if (dto.categoryId() != null) templateEntity.setCategoryId(dto.categoryId());
        if (dto.tags() != null) templateEntity.setTags(dto.tags());
        if (dto.madeForKids() != null) templateEntity.setMadeForKids(dto.madeForKids());

        templateEntity = templateRepository.save(templateEntity);

        return TemplateMapper.fromTemplateEntityToChangeTemplateResponseDto(templateEntity);
    }
}
