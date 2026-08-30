package com.audioslide.upload.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VideoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;


    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrivacyStatus privacyStatus;

    private Integer categoryId;

    @ElementCollection
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private Instant publishAt;

    @Column(nullable = false)
    private Boolean madeForKids;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus videoStatus;

    @Column(nullable = false)
    private String thumbnailS3Key;

    private String videoS3Key;


    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }
}
