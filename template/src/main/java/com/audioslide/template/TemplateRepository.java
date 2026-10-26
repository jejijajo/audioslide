package com.audioslide.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity ,Long> {

    Optional<List<TemplateEntity>> findAllByUserId(Long userId);
    Optional<TemplateEntity> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndTemplateName(Long userId, String templateName);
    long countByUserId(Long userId);
}
