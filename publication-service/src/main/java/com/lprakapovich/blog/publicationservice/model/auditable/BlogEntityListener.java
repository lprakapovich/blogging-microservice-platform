package com.lprakapovich.blog.publicationservice.model.auditable;

import com.lprakapovich.blog.publicationservice.model.Blog;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class BlogEntityListener {

    @PrePersist
    public void onPrePersist(Blog blog) {
        blog.setCreatedDateTime(LocalDateTime.now());
    }

    @PreUpdate
    public void onPreUpdate(Blog blog) {
        blog.setUpdatedDateTime(LocalDateTime.now());
    }
}
