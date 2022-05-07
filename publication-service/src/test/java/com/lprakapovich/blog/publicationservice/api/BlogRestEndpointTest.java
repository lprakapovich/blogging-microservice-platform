package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CreateBlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateBlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.utils.GenericMappingUtils;
import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.PrincipalMismatchException;
import com.lprakapovich.blog.publicationservice.feign.AuthenticationServerClient;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.service.SubscriptionService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import com.lprakapovich.blog.publicationservice.util.UriBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.lprakapovich.blog.publicationservice.util.AuthenticationMockUtils.*;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.BLOG_ID;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getDefaultBlogId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BlogRestEndpoint.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BlogRestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogService blogService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private BlogOwnershipValidator blogOwnershipValidator;

    @MockBean
    private GenericMappingUtils mappingUtils;

    @MockBean
    private AuthenticationServerClient authorizationClient;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mockTokenValidationWithDefaultPrincipal(authorizationClient);
    }

    @Test
    void whenDtoValidationSucceeds_return200() throws Exception {

        // given
        CreateBlogDto dto = new CreateBlogDto();
        dto.setId(BLOG_ID);
        String expectedLocationHeader = UriBuilder.build(BLOG_ID, DEFAULT_PRINCIPAL).toString();
        given(blogService.createBlog(any())).willReturn(getDefaultBlogId());

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/publication-service/blogs")
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).isEqualTo(expectedLocationHeader);
    }

    @Test
    void whenDtoValidationFailsOnMissingBlogId_return400() throws Exception {

        // given
        CreateBlogDto dto = new CreateBlogDto();

        // when
        // then
        mockMvc.perform(post("/publication-service/blogs")
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateDtoWithEmptyFields_noExceptionIsThrown() throws Exception {

        // given
        UpdateBlogDto dto = new UpdateBlogDto();

        // when
        MvcResult mvcResult = mockMvc.perform(put("/publication-service/blogs/{id},{username}", BLOG_ID, DEFAULT_PRINCIPAL)
                .header(AUTHORIZATION, TOKEN)
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
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).isPrincipalOwner(any());

        // when
        // then
        mockMvc.perform(put("/publication-service/blogs/{id},{username}", BLOG_ID, DEFAULT_PRINCIPAL)
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenBlogExistenceCheckFailsOnUpdate_return404() throws Exception {

        // given
        UpdateBlogDto dto = new UpdateBlogDto();
        doThrow(new BlogNotFoundException()).when(blogOwnershipValidator).isPrincipalOwner(any());

        // when
        // then
        mockMvc.perform(put("/publication-service/blogs/{id},{username}", BLOG_ID, DEFAULT_PRINCIPAL)
                .header(AUTHORIZATION, TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenBlogOwnershipValidationFailsOnDelete_return403() throws Exception {

        // given
        doThrow(new PrincipalMismatchException()).when(blogOwnershipValidator).isPrincipalOwner(any());

        // when
        // then
        mockMvc.perform(delete("/publication-service/blogs/{id},{username}", BLOG_ID, DEFAULT_PRINCIPAL)
                .header(AUTHORIZATION, TOKEN))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void whenBlogExistenceCheckFailsOnDelete_return404() throws Exception {

        // given
        doThrow(new BlogNotFoundException()).when(blogOwnershipValidator).isPrincipalOwner(any());

        // when
        // then
        mockMvc.perform(delete("/publication-service/blogs/{id},{username}", BLOG_ID, DEFAULT_PRINCIPAL)
                .header(AUTHORIZATION, TOKEN))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void whenBlogIdUniquenessPerUserCheckFails_return406() throws Exception {

        // given
        given(blogService.existsById(BLOG_ID)).willReturn(true);

        // when
        // then
        mockMvc.perform(post("/publication-service/blogs/check")
                .param("blogId", BLOG_ID)
                .header(AUTHORIZATION, TOKEN))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void whenBlogIdUniquenessPerUserSucceeds_return200() throws Exception {

        // given
        given(blogService.existsById(BLOG_ID)).willReturn(false);

        // when
        // then
        mockMvc.perform(post("/publication-service/blogs/check")
                .param("blogId", BLOG_ID)
                .header(AUTHORIZATION, TOKEN))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
