package com.audioslide.template;

import com.audioslide.template.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/template")
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping("/health")
    public String health() {return "Template Service is running...";}

    @PostMapping("/create")
    public CreateTemplateResponseDto createTemplate(
            @RequestBody CreateTemplateRequestDto createTemplateRequestDto,
            @AuthenticationPrincipal String userId) {
        return templateService.createTemplate(createTemplateRequestDto,Long.parseLong(userId));
    }

    @DeleteMapping("/{id}/delete")
    public void deleteTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal String userId) {
        templateService.deleteTemplate(id, Long.parseLong(userId));
    }

    @GetMapping
    public ReadAllTemplatesResponseDto allTemplates(@AuthenticationPrincipal String userId) {
        return templateService.readAllTemplates(Long.parseLong(userId));
    }

    @GetMapping("/{id}")
    public ReadTemplateResponseDto allTemplates(
            @PathVariable Long id,
            @AuthenticationPrincipal String userId) {
        return templateService.readTemplate(id, Long.parseLong(userId));
    }

    @PatchMapping("/{id}")
    public ChangeTemplateResponseDto changeTemplate(
            @PathVariable Long id,
            @AuthenticationPrincipal String userId,
            @RequestBody ChangeTemplateRequestDto changeTemplateRequestDto
            ) {
        return templateService.changeTemplate(id, Long.parseLong(userId), changeTemplateRequestDto);
    }
}
