package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PublicationRepositoryTest {

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    public void whenPublicationIsPersisted_thenContentIsSavedAsJsonb() {

        Publication publication = new Publication();
        publication.setStatus(Status.PUBLISHED);
        String content = "<h1> Hi </h1>";
        publication.setContent(content);

        Publication persistedPublication = publicationRepository.save(publication);
        Optional<Publication> optionalById = publicationRepository.findById(persistedPublication.getId());

        assertThat(optionalById).isPresent();
        Publication persistedPublicationById = optionalById.get();

        assertThat(persistedPublicationById).isNotNull();
        assertThat(persistedPublicationById.getId()).isEqualTo(persistedPublication.getId());
        assertThat(persistedPublicationById.getStatus()).isEqualTo(persistedPublication.getStatus());
        assertThat(persistedPublicationById.getContent()).isEqualTo(persistedPublication.getContent());
        assertThat(persistedPublicationById.getCreatedDateTime()).isBefore(LocalDateTime.now());
        assertThat(persistedPublicationById.getUpdatedDateTime()).isNull();
    }
}
