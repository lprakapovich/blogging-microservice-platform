package com.lprakapovich.blog.publicationservice.model;

import com.lprakapovich.blog.publicationservice.model.auditable.AuditableEntity;
import com.lprakapovich.blog.publicationservice.model.auditable.BlogEntityListener;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(BlogEntityListener.class)
@Table(name = "blogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog extends AuditableEntity {

    @EmbeddedId
    private BlogId id;
    private String displayName;
    private String description;

    @OneToMany(mappedBy = "blog")
    private List<Category> categories = new ArrayList<>();

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    @EqualsAndHashCode
    public static class BlogId implements Serializable {

        @Column(name = "blog_id", nullable = false)
        private String id;

        @Column(name = "username", nullable = false)
        private String username;
    }
}
