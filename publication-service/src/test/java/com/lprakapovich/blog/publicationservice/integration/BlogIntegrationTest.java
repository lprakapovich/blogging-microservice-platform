package com.lprakapovich.blog.publicationservice.integration;

import com.lprakapovich.blog.publicationservice.api.dto.BlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.CreateBlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateBlogDto;
import com.lprakapovich.blog.publicationservice.feign.AuthorizationClient;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.repository.BlogRepository;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import com.lprakapovich.blog.publicationservice.util.UriBuilder;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.lprakapovich.blog.publicationservice.util.AuthenticationMockUtils.*;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.*;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(provider = ZONKY, refresh = AFTER_EACH_TEST_METHOD)
class BlogIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @MockBean
    private AuthorizationClient authorizationClient;

    private static final String BLOG_URL = "/publication-service/blogs";

    private static final String CUSTOM_PRINCIPAL = "customPrincipal";

    @BeforeEach
    void setUp() {
        mockTokenValidationWithDefaultPrincipal(authorizationClient);
    }

    @Test
    void whenNoErrorOnCreate_blogUsernameIsResolvedFromSecurityContextPrincipal_return201() {

        // given
        CreateBlogDto dto = new CreateBlogDto();
        dto.setId(BLOG_ID);
        URI expectedLocation = UriBuilder.build(BLOG_ID, DEFAULT_PRINCIPAL);
        BlogId expectedBlogId = getDefaultBlogId();

        HttpHeaders headers = getAuthorizationHeaders();
        HttpEntity<CreateBlogDto> request = new HttpEntity<>(dto, headers);

        // when
        ResponseEntity<URI> responseEntity = testRestTemplate.postForEntity(
                BLOG_URL,
                request,
                URI.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isEqualTo(expectedLocation);

        Optional<Blog> createdOptional = blogRepository.findById(expectedBlogId);
        assertThat(createdOptional).isPresent();

        Blog createdBlog = createdOptional.get();
        assertThat(createdBlog.getId()).isEqualTo(expectedBlogId);
        assertThat(createdBlog.getDisplayName()).isNull();
        assertThat(createdBlog.getDescription()).isNull();
        assertThat(createdBlog.getCreatedDateTime()).isBefore(LocalDateTime.now());
    }

    @Test
    void whenMissingAuthHeaderOnCreate_return403() {

        // given
        HttpEntity<CreateBlogDto> request = new HttpEntity<>(new CreateBlogDto());

        // when
        ResponseEntity<URI> responseEntity = testRestTemplate.postForEntity(
                BLOG_URL,
                request,
                URI.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void whenUsernameMismatchOnUpdate_return403() {

        // given
        HttpHeaders headers = getAuthorizationHeaders();
        HttpEntity<UpdateBlogDto> request = new HttpEntity<>(new UpdateBlogDto(), headers);

        // when
        ResponseEntity<BlogDto> responseEntity = testRestTemplate.exchange(
                BLOG_URL + "/{id},{username}",
                HttpMethod.PUT,
                request,
                BlogDto.class,
                BLOG_ID,
                CUSTOM_PRINCIPAL
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void whenUsernameMismatchOnDelete_return403() {

        // given
        saveDefaultBlog();
        HttpHeaders headers = getAuthorizationHeaders();
        HttpEntity<UpdateBlogDto> request = new HttpEntity<>(headers);

        // when
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                BLOG_URL + "/{id},{username}",
                HttpMethod.DELETE,
                request,
                Void.class,
                BLOG_ID,
                CUSTOM_PRINCIPAL
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(blogRepository.findById(getDefaultBlogId())).isPresent();
    }

    @Test
    void whenNoErrorOnDelete_blogResourcesDeleted_return204() {

        // given
        Blog blog = saveDefaultBlog();
        BlogId blogId = blog.getId();

        Publication publication = new Publication();
        publication.setTitle("title");
        publication.setContent("content");
        publication.setBlog(blog);
        publicationRepository.save(publication);

        Category category1 = new Category();
        category1.setName("name1");
        category1.setBlog(blog);

        Category category2 = new Category();
        category2.setName("name2");
        category2.setBlog(blog);
        categoryRepository.saveAll(List.of(category1, category2));

        HttpHeaders headers = getAuthorizationHeaders();
        HttpEntity<UpdateBlogDto> request = new HttpEntity<>(headers);

        // when
        ResponseEntity<Void> responseEntity = testRestTemplate.exchange(
                BLOG_URL + "/{id},{username}",
                HttpMethod.DELETE,
                request,
                Void.class,
                BLOG_ID,
                DEFAULT_PRINCIPAL
        );

        // then
        assertThat(blogRepository.findById(blogId)).isEmpty();
        assertThat(categoryRepository.findByBlogId(blogId.getId(), blogId.getUsername())).isEmpty();
        assertThat(publicationRepository.findByBlog_Id(blogId)).isEmpty();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void whenSearchedByCriteria_principalBlogsFilteredOut_return200() {

        // given
        saveBlog("blog1", DEFAULT_PRINCIPAL);
        saveBlog("blog2", DEFAULT_PRINCIPAL);
        saveBlog("blog3", CUSTOM_PRINCIPAL);
        saveBlog("blog4", CUSTOM_PRINCIPAL);

        HttpHeaders headers = getAuthorizationHeaders();
        HttpEntity<List<BlogDto>> request = new HttpEntity<>(headers);

        // when
        ResponseEntity<List<BlogDto>> responseEntity = testRestTemplate.exchange(
                BLOG_URL + "/search?criteria={criteria}",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {},
                "blog"
        );

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<BlogDto> dtos = responseEntity.getBody();
        assertThat(dtos)
                .noneMatch(dto -> dto.getId().getUsername().equals(DEFAULT_PRINCIPAL))
                .allMatch(dto -> dto.getId().getUsername().equals(CUSTOM_PRINCIPAL));
    }

    private Blog saveDefaultBlog() {
       return blogRepository.save(getDefaultBlog());
    }

    private void saveBlog(String blogId, String username) {
        blogRepository.save(getBlog(blogId, username));
    }
}
