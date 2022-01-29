package com.lprakapovich.blog.publicationservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "categories",
        uniqueConstraints = @UniqueConstraint(name = "CategoryNamePerBlogConstraint", columnNames = {"name", "blog_id"}))
@Data
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue
    private long id;
    private String name;

    public Category(String name) {
        this.name = name;
    }
}
