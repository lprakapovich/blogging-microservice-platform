package com.lprakapovich.blog.publicationservice.model;

import com.lprakapovich.blog.publicationservice.model.auditable.AuditableEntity;
import com.lprakapovich.blog.publicationservice.model.auditable.PublicationEntityListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@EntityListeners(PublicationEntityListener.class)
@Table(name = "publications")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Publication extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private Status status;

    @ManyToOne
    @JoinColumn(name="blog_id", referencedColumnName = "blog_id")
    @JoinColumn(name="blog_username", referencedColumnName = "username")
    private Blog blog;

    @ManyToOne
    @JoinColumn
    private Category category;

    @Column(columnDefinition = "text")
    private String content;
}
