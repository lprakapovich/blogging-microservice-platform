package com.lprakapovich.blog.publicationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "categories", uniqueConstraints =
        { @UniqueConstraint(columnNames = { "name", "blog_id", "username" }) })
@Data
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name="blog_id", referencedColumnName = "blog_id")
    @JoinColumn(name="username", referencedColumnName = "username")
    @JsonIgnore
    @NotNull
    private Blog blog;
}
