package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CreateBlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateBlogDto;
import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.PrincipalMismatchException;
import com.lprakapovich.blog.publicationservice.exception.handler.GlobalExceptionHandler;
import com.lprakapovich.blog.publicationservice.feign.AuthorizationClient;
import com.lprakapovich.blog.publicationservice.security.JwtAuthorizationFilter;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.service.SubscriptionService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import com.lprakapovich.blog.publicationservice.util.UriBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import static com.lprakapovich.blog.publicationservice.util.AuthenticationMockUtils.*;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = BlogRestEndpoint.class)
//@ImportAutoConfiguration({FeignAutoConfiguration.class})
class BlogRestEndpointTest {

    @MockBean
    private BlogService blogService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SubscriptionService subscriptionService;

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

    private BlogRestEndpoint blogRestEndpoint;

    @BeforeEach
     void init() {
        blogRestEndpoint = new BlogRestEndpoint(
                blogService,
                categoryService,
                subscriptionService,
                blogOwnershipValidator
        );
    }

    @Test
    void whenDtoValidationSucceeds_return200() throws Exception {

        // given
        CreateBlogDto dto = new CreateBlogDto();
        dto.setId(BLOG_ID);
        String expectedLocationHeader = UriBuilder.build(BLOG_ID, USERNAME).toString();
        given(blogService.createBlog(any())).willReturn(getDefaultBlogId());
        mockSuccessfulTokenValidation(authorizationClient);

        // when
        MvcResult mvcResult = standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .build()
                .perform(post("/publication-service/blogs")
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andReturn();

        // then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).isEqualTo(expectedLocationHeader);
    }

    @Test
    void whenDtoValidationFailsOnMissingBlogId_return400() throws Exception {

        // given
        CreateBlogDto dto = new CreateBlogDto();
        mockSuccessfulTokenValidation(authorizationClient);

        // when
        // then
        standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .build()
                .perform(post("/publication-service/blogs")
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateDtoWithEmptyFields_noExceptionIsThrown() throws Exception {

        // given
        UpdateBlogDto dto = new UpdateBlogDto();
        mockSuccessfulTokenValidation(authorizationClient);

        // when
        MvcResult mvcResult = standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .build()
                .perform(put("/publication-service/blogs/{id},{username}", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andReturn();

        // then
        MockHttpServletResponse response = mvcResult.getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void whenBlogOwnershipValidationFailsOnUpdate_return403() throws Exception {

        // given
        UpdateBlogDto dto = new UpdateBlogDto();
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).validate(any());

        // when
        // then
        standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(put("/publication-service/blogs/{id},{username}", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenBlogExistenceCheckFailsOnUpdate_return404() throws Exception {

        // given
        UpdateBlogDto dto = new UpdateBlogDto();
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new BlogNotFoundException()).when(blogOwnershipValidator).validate(any());

        // when
        // then
        standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(put("/publication-service/blogs/{id},{username}", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenBlogOwnershipValidationFailsOnDelete_return403() throws Exception {

        // given
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).validate(any());

        // when
        // then
       standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(delete("/publication-service/blogs/{id},{username}", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenBlogExistenceCheckFailsOnDelete_return404() throws Exception {

        // given
        mockSuccessfulTokenValidation(authorizationClient);
        doThrow(new BlogNotFoundException()).when(blogOwnershipValidator).validate(any());

        // when
        // then
        standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(delete("/publication-service/blogs/{id},{username}", BLOG_ID, USERNAME)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenBlogIdUniquenessPerUserCheckFails_return406() throws Exception {

        // given
        mockSuccessfulTokenValidation(authorizationClient);
        given(blogService.existsById(BLOG_ID)).willReturn(true);

        // when
        standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(post("/publication-service/blogs/check")
                        .param("blogId", BLOG_ID)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void whenBlogIdUniquenessPerUserSucceeds_return200() throws Exception {

        // given
        mockSuccessfulTokenValidation(authorizationClient);
        given(blogService.existsById(BLOG_ID)).willReturn(false);

        // when
        standaloneSetup(blogRestEndpoint)
                .addFilter(jwtAuthFilter)
                .setControllerAdvice(exceptionHandler)
                .build()
                .perform(post("/publication-service/blogs/check")
                        .param("blogId", BLOG_ID)
                        .header(AUTH_HEADER, TOKEN))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
