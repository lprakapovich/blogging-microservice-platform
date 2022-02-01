package com.lprakapovich.blog.publicationservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;

@Slf4j
@Entity
@Table(name = "publications", indexes = {
        @Index(name = "publicationId_blogId_index", columnList = "id, blog_id"),
        @Index(name = "blogId_index", columnList = "blog_id")})
@Data
@NoArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String header;
    private String subHeader;

    private Status status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastUpdatedDateTime;

    @ManyToOne
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne
    @JoinColumn
    private Category category;

    @PrePersist
    public void publicationPrePersist() {
        this.createdDateTime = LocalDateTime.now();
        log.info("publicationPrePersist() called");
    }

    @PreUpdate
    public void publicationPreUpdate() {
        this.lastUpdatedDateTime = LocalDateTime.now();
        log.info("publicationPreUpdate() called");
    }
}
