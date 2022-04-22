package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CreatePublicationDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdatePublicationDto;
import com.lprakapovich.blog.publicationservice.exception.PrincipalMismatchException;
import com.lprakapovich.blog.publicationservice.exception.handler.GlobalExceptionHandler;
import com.lprakapovich.blog.publicationservice.feign.AuthorizationClient;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Status;
import com.lprakapovich.blog.publicationservice.security.JwtAuthorizationFilter;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.PublicationService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static com.lprakapovich.blog.publicationservice.util.AuthenticationMockUtils.*;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = PublicationRestEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PublicationRestEndpointTest {

    @MockBean
    private PublicationService publicationService;

    @MockBean
    private BlogService blogService;

    @MockBean
    private BlogOwnershipValidator blogOwnershipValidator;

    @MockBean
    private AuthorizationClient authorizationClient;

    @Autowired
    private GlobalExceptionHandler exceptionHandler;

    @Autowired
    private JwtAuthorizationFilter jwtAuthFilter;

    @Autowired
    private ObjectMapper mapper;

    private PublicationRestEndpoint publicationRestEndpoint;

    @BeforeAll
    void init() {
        publicationRestEndpoint = new PublicationRestEndpoint(
                publicationService,
                blogService,
                mapper,
                blogOwnershipValidator
        );
    }

    @BeforeEach
    void setUp() {
        mockSuccessfulTokenValidation(authorizationClient);
    }

    @Test
    void whenDtoValidationFailsOnCreate_return400() throws Exception {

        // given
        CreatePublicationDto dto = new CreatePublicationDto();

        // when
        // then
        standaloneSetup(publicationRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(post("/publication-service/{blogId},{username}/publications", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenBlogOwnershipValidationFailsOnCreate_return403() throws Exception {

        // given
        CreatePublicationDto dto = new CreatePublicationDto();
        dto.setStatus(Status.PUBLISHED);
        dto.setTitle("title");
        dto.setContent("content");
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).validate(any());

        // when
        // then
        standaloneSetup(publicationRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(post("/publication-service/{blogId},{username}/publications", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenDtoValidationFailsOnUpdate_return400() throws Exception {

        // given
        UpdatePublicationDto dto = new UpdatePublicationDto();

        // when
        // then
        standaloneSetup(publicationRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(put("/publication-service/{blogId},{username}/publications/{id}", BLOG_ID, USERNAME, 1L)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenBlogOwnershipValidationFailsOnUpdate_return403() throws Exception {

        // given
        UpdatePublicationDto dto = new UpdatePublicationDto();
        dto.setStatus(Status.DRAFT);
        dto.setTitle("title");
        dto.setContent("content");
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).validate(any());


        // when
        // then
        standaloneSetup(publicationRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(put("/publication-service/{blogId},{username}/publications/{id}", BLOG_ID, USERNAME, 1L)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenBlogOwnershipValidationFailsOnDelete_return403() throws Exception {

        // given
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).validate(any());

        // when
        // then
        standaloneSetup(publicationRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(delete("/publication-service/{blogId},{username}/publications/{id}", BLOG_ID, USERNAME, 1L)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenGettingPublication_principalIsOwner_getById() throws Exception {

        // given
        long publicationId = 1L;
        BlogId expectedBlogId = getDefaultBlogId();
        given(blogService.isOwner(expectedBlogId.getId())).willReturn(true);

        // when
       standaloneSetup(publicationRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(get("/publication-service/{blogId},{username}/publications/{id}", BLOG_ID, USERNAME, publicationId)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        verify(publicationService).getById(publicationId, expectedBlogId);
        verify(publicationService, never()).getByIdAndStatus(publicationId, expectedBlogId, Status.PUBLISHED);

    }

    @Test
    void whenGettingPublication_principalIsNotOwner_getOnlyPublished() throws Exception {

        // given
        long publicationId = 1L;
        BlogId expectedBlogId = getDefaultBlogId();
        given(blogService.isOwner(expectedBlogId.getId())).willReturn(false);

        // when
        standaloneSetup(publicationRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(get("/publication-service/{blogId},{username}/publications/{id}", BLOG_ID, USERNAME, publicationId)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        verify(publicationService).getByIdAndStatus(publicationId, expectedBlogId, Status.PUBLISHED);
        verify(publicationService, never()).getById(publicationId, expectedBlogId);
    }
}
