package com.lprakapovich.blog.publicationservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.BlogDto;
import com.lprakapovich.blog.publicationservice.exception.error.ErrorEnvelope;
import com.lprakapovich.blog.publicationservice.feign.AuthenticationServerClient;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.model.Status;
import com.lprakapovich.blog.publicationservice.repository.BlogRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.AuthenticationMockUtils.*;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.BLOG_ID;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getDefaultBlog;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(provider = ZONKY, refresh = AFTER_EACH_TEST_METHOD)
class PublicationIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationServerClient authorizationClient;

    private static final String PUBLICATION_URL = "/publication-service/{blogId},{username}/publications";

    private static final String CUSTOM_PRINCIPAL = "customPrincipal";


    @BeforeEach
    void setUp() {
        mockTokenValidationWithDefaultPrincipal(authorizationClient);
    }

    @Test
    void whenCategoryIsAssignedToPublication_blogOwnershipValidationFails_return403() {

        // given
        HttpHeaders headers = getAuthorizationHeaders();
        HttpEntity<List<BlogDto>> request = new HttpEntity<>(headers);

        // when
        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(
                PUBLICATION_URL + "/{id}/{categoryId}",
                HttpMethod.PUT,
                request,
                Object.class,
                BLOG_ID,
                CUSTOM_PRINCIPAL,
                1L,
                1L
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void whenCategoryIsAssignedToPublication_categoryDoNotExists_return404() {

        // given
        Blog blog = saveDefaultBlog();
        Publication publication = savePublication(blog);

        HttpHeaders headers = getAuthorizationHeaders();
        HttpEntity<List<BlogDto>> request = new HttpEntity<>(headers);

        // when
        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(
                PUBLICATION_URL + "/{id}/{categoryId}",
                HttpMethod.PUT,
                request,
                Object.class,
                BLOG_ID,
                DEFAULT_PRINCIPAL,
                publication.getId(),
                1L
        );

        // then
        ErrorEnvelope errorEnvelope = objectMapper.convertValue(responseEntity.getBody(), ErrorEnvelope.class);
        assertThat(errorEnvelope.getResponseStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(errorEnvelope.getMessage()).isEqualTo("Category not found");
    }

    private Blog saveDefaultBlog() {
        return blogRepository.save(getDefaultBlog());
    }

    private Publication savePublication(Blog blog) {
        Publication publication = new Publication();
        publication.setTitle("title");
        publication.setContent("content");
        publication.setStatus(Status.DRAFT);
        publication.setBlog(blog);
        return publicationRepository.save(publication);
    }
}
