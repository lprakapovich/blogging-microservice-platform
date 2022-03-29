package com.lprakapovich.blog.publicationservice.model.auditable;

import com.lprakapovich.blog.publicationservice.model.Publication;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class PublicationEntityListener {

    @PrePersist
    public void onPrePersist(Publication publication) {
        publication.setCreatedDateTime(LocalDateTime.now());
    }

    @PreUpdate
    public void onPreUpdate(Publication publication) {
        publication.setUpdatedDateTime(LocalDateTime.now());
    }
}
