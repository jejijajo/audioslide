package com.audioslide.template;

import com.audioslide.template.dto.CreateTemplateRequestDto;
import com.audioslide.template.dto.CreateTemplateResponseDto;
import com.audioslide.template.dto.ReadAllTemplatesResponseDto;
import com.audioslide.template.dto.ReadTemplateResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    @Transactional
    public CreateTemplateResponseDto createTemplate(CreateTemplateRequestDto dto, Long userId) {
        TemplateEntity templateEntity = TemplateMapper.fromCreateTemplateRequestDtoToTemplateEntity(dto, userId);
        TemplateEntity savedTemplateEntity = templateRepository.save(templateEntity);
        return TemplateMapper.fromTemplateEntityToCreateTemplateResponseDto(savedTemplateEntity);
    }

    @Transactional(readOnly = true)
    public ReadTemplateResponseDto readTemplate(Long templateId, Long userId) throws Exception {
        TemplateEntity templateEntity = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new Exception("Failed to find template"));
        return TemplateMapper.fromTemplateEntityToReadTemplateResponseDto(templateEntity);
    }

    @Transactional(readOnly = true)
    public ReadAllTemplatesResponseDto readAllTemplates(Long userId) throws Exception {
        List<TemplateEntity> templates = templateRepository.findAllByUserId(userId)
                .orElseThrow(() -> new Exception("Failed to find templates"));
        return TemplateMapper.fromTemplateEntityListToReadAllTemplatesResponseDto(templates);
    }

    @Transactional
    public void deleteTemplate(Long templateId, Long userId) throws Exception {
        TemplateEntity templateEntity = templateRepository.findByIdAndUserId(templateId, userId)
                .orElseThrow(() -> new Exception("Failed to find template"));

        templateRepository.delete(templateEntity);
    }
}
