package com.audioslide.template;

import com.audioslide.template.dto.CreateTemplateRequestDto;
import com.audioslide.template.dto.CreateTemplateResponseDto;
import com.audioslide.template.dto.ReadAllTemplatesResponseDto;
import com.audioslide.template.dto.ReadTemplateResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public ReadTemplateResponseDto readTemplate(Long templateId, Long userId) throws Exception {
        TemplateEntity templateEntity = templateRepository.findById(templateId)
                .orElseThrow(() -> new Exception("Failed to find template"));
        if(!userId.equals(templateEntity.getUserId())) { throw new Exception("Failed to read template"); }
        return TemplateMapper.fromTemplateEntityToReadTemplateResponseDto(templateEntity);
    }

    public ReadAllTemplatesResponseDto readAllTemplates(Long userId) throws Exception {
        List<TemplateEntity> templates = templateRepository.findAllByUserId(userId)
                .orElseThrow(() -> new Exception("Failed to find templates"));
        return TemplateMapper.fromTemplateEntityListToReadAllTemplatesResponseDto(templates);
    }

    @Transactional
    public void deleteTemplate(Long templateId, Long userId) throws Exception {
        TemplateEntity templateEntity = templateRepository.findById(templateId)
                .orElseThrow(() -> new Exception("Failed to find template"));
        templateRepository.delete(templateEntity);
    }
}
