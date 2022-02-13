package com.lprakapovich.blog.publicationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "blogs",
        uniqueConstraints = @UniqueConstraint(name = "BlogIdUsernameConstraint", columnNames = {"id", "username"}),
        indexes = @Index(name = "blogId_index", columnList = "id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {

    @Id
    private String id;

    private String username;

    private String name;

    private String description;

    private LocalDateTime createdDateTime;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "blog_id")
    private List<Category> categories = new ArrayList<>();

    @PrePersist
    public void publicationPrePersist() {
        this.createdDateTime = LocalDateTime.now();
    }
}
