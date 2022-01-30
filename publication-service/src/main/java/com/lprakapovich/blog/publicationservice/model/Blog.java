package com.lprakapovich.blog.publicationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "blogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {

    @Id
    private String id;

    private String description;
    private Instant creationDate;

    @Embedded
    private Author author;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "blog_id")
    private List<Category> categories = new ArrayList<>();



//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "blog")
//    @JoinColumn(name = "blog_id")
//    private List<Publication> publications = new ArrayList<>();
}
