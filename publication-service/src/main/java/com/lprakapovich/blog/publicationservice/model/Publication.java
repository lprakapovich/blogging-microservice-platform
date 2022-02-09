package com.lprakapovich.blog.publicationservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Type(type = "com.lprakapovich.blog.publicationservice.postgres.ContentJsonbType")
    private Content content;

    @PrePersist
    public void publicationPrePersist() {
        this.createdDateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void publicationPreUpdate() {
        this.lastUpdatedDateTime = LocalDateTime.now();
    }
}
