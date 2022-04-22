package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CreateCategoryDto;
import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.CategoryNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.PrincipalMismatchException;
import com.lprakapovich.blog.publicationservice.exception.handler.GlobalExceptionHandler;
import com.lprakapovich.blog.publicationservice.feign.AuthorizationClient;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.security.JwtAuthorizationFilter;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static com.lprakapovich.blog.publicationservice.util.AuthenticationMockUtils.*;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = CategoryRestEndpoint.class)
class CategoryRestEndpointTest {

    @MockBean
    private CategoryService categoryService;

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

    private CategoryRestEndpoint categoryRestEndpoint;

    @BeforeEach
    void init() {
        categoryRestEndpoint = new CategoryRestEndpoint(
                categoryService,
                blogOwnershipValidator
        );
    }

    @Test
    void whenDtoValidationSucceedsOnCreate_return200() throws Exception {

        // given
        BlogId expectedBlogId = getDefaultBlogId();
        String expectedCategoryName = "name";
        CreateCategoryDto dto = new CreateCategoryDto();
        dto.setName(expectedCategoryName);
        mockSuccessfulTokenValidation(authorizationClient);

        // when
        standaloneSetup(categoryRestEndpoint)
                .addFilter(jwtAuthFilter)
                .build()
                .perform(post("/publication-service/{blogId},{username}/categories", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        ArgumentCaptor<BlogId> blogIdCaptor = ArgumentCaptor.forClass(BlogId.class);
        verify(categoryService).createCategory(blogIdCaptor.capture(), eq(expectedCategoryName));
        assertThat(blogIdCaptor.getValue()).isEqualTo(expectedBlogId);
    }

    @Test
    void whenDtoValidationFailsOnCreate_return400() throws Exception{

        // given
        CreateCategoryDto dto = new CreateCategoryDto();
        mockSuccessfulTokenValidation(authorizationClient);

        // when
        standaloneSetup(categoryRestEndpoint)
                .addFilter(jwtAuthFilter)
                .build()
                .perform(post("/publication-service/{blogId},{username}/categories", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // then
        verify(categoryService, never()).createCategory(any(), anyString());
    }

    @Test
    void whenBlogOwnershipValidationFailsOnCreate_return403() throws Exception {

        // given
        CreateCategoryDto dto = new CreateCategoryDto();
        dto.setName("name");
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).validate(any());

        // when
        standaloneSetup(categoryRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(post("/publication-service/{blogId},{username}/categories", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isForbidden());

        // then
        verify(categoryService, never()).createCategory(any(), eq("name"));
    }

    @Test
    void whenSucceedsOnDelete_returns204() throws Exception {

        // given
        long categoryId = 1L;
        BlogId expectedBlogId = getDefaultBlogId();
        mockSuccessfulTokenValidation(authorizationClient);

        // when
        standaloneSetup(categoryRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(delete("/publication-service/{blogId},{username}/categories/{id}",
                        BLOG_ID, USERNAME, categoryId)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isNoContent());

        // then
        verify(categoryService).deleteCategory(expectedBlogId, categoryId);
    }

    @Test
    void whenBlogOwnershipValidationFailsOnDelete_return403() throws Exception {

        // given
        long categoryId = 1L;
        BlogId expectedBlogId = getDefaultBlogId();
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).validate(any());

        // when
        standaloneSetup(categoryRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(delete("/publication-service/{blogId},{username}/categories/{id}",
                        BLOG_ID, USERNAME, categoryId)
                    .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());

        // then
        verify(categoryService, never()).deleteCategory(expectedBlogId, categoryId);
    }

    @Test
    void whenBlogExistenceCheckFailsOnDelete_return404() throws Exception{

        // given
        long categoryId = 1L;
        BlogId expectedBlogId = getDefaultBlogId();
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new BlogNotFoundException()).when(categoryService).deleteCategory(expectedBlogId, categoryId);

        // when
        // then
        standaloneSetup(categoryRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(delete("/publication-service/{blogId},{username}/categories/{id}",
                        BLOG_ID, USERNAME, categoryId)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCategoryExistenceCheckFailsOnDelete_return404() throws Exception {

        // given
        long categoryId = 1L;
        BlogId expectedBlogId = getDefaultBlogId();
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new CategoryNotFoundException()).when(categoryService).deleteCategory(expectedBlogId, categoryId);

        // when
        // then
        standaloneSetup(categoryRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(delete("/publication-service/{blogId},{username}/categories/{id}",
                        BLOG_ID, USERNAME, categoryId)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
