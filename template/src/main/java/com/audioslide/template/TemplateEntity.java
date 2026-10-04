package com.audioslide.template;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "templates", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "template_name"}))
@NoArgsConstructor
@Getter
@Setter
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 100)
    private String templateName;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 4500)
    private String description;

    private Integer categoryId;

    @ElementCollection
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag", length = 500)
    private List<String> tags;

    @Column(nullable = false)
    private Boolean madeForKids;
}
