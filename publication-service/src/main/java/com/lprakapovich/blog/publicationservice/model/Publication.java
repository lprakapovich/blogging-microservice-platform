package com.lprakapovich.blog.publicationservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne
    @JoinColumn
    private Category category;
}
