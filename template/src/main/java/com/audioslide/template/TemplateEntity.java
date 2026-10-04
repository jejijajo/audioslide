package com.audioslide.template;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "templates")
@NoArgsConstructor
@Getter
@Setter
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String templateName;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    private String description;

    private Integer categoryId;

    @ElementCollection
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false)
    private Boolean madeForKids;
}
