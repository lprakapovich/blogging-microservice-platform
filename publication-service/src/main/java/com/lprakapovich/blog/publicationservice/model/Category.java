package com.lprakapovich.blog.publicationservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "categories",
        uniqueConstraints = @UniqueConstraint(name = "CategoryNamePerBlogConstraint", columnNames = {"name", "blog_id"}),
        indexes = @Index(name = "categoryId_blogId_index", columnList = "id, blog_id")
)
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

    public Category(Category category) {
        this.name = category.name;
    }
}
