package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.DuplicatedBlogIdException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.repository.BlogRepository;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.lprakapovich.blog.publicationservice.util.BlogUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @InjectMocks
    private BlogService blogService;

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Test
    void whenIdNotUnique_exceptionIsThrown() {

        // given
        Blog blog = getDefaultBlog();
        given(blogRepository.existsById(blog.getId())).willReturn(true);

        // when
        // then
        assertThrows(DuplicatedBlogIdException.class, () -> blogService.createBlog(blog));
    }

    @Test
    void whenIdIsUnique_blogIsCreated() {

        // given
        Blog blog = getDefaultBlog();
        given(blogRepository.existsById(blog.getId())).willReturn(false);
        given(blogRepository.save(any())).willReturn(blog);

        // when
        blogService.createBlog(blog);
        ArgumentCaptor<Blog> blogCaptor = ArgumentCaptor.forClass(Blog.class);

        // then
        verify(blogRepository).save(blogCaptor.capture());
        Blog capturedBlog = blogCaptor.getValue();
        assertEquals(blog, capturedBlog);
    }

    @Test
    void whenBlogIdUpdated_fieldsAreUpdatedProperly() {

        // given
        Blog blog = getDefaultBlog();
        String displayName = "displayName";
        String description = "description";
        blog.setDisplayName(displayName);
        blog.setDescription(description);
        given(blogRepository.findById(blog.getId())).willReturn(Optional.of(blog));


        // when
        blogService.updateBlog(blog.getId(), blog);
        ArgumentCaptor<Blog> blogCaptor = ArgumentCaptor.forClass(Blog.class);


        // then
        verify(blogRepository).save(blogCaptor.capture());
        Blog capturedBlog = blogCaptor.getValue();
        assertEquals(blog, capturedBlog);
    }

    @Test
    void whenEmptyNameAndDisplayNameSet_exceptionIsNotThrown() {

        // given
        Blog blog = getDefaultBlog();
        given(blogRepository.findById(blog.getId())).willReturn(Optional.of(blog));

        // when
        // then
        assertDoesNotThrow(() -> blogService.updateBlog(blog.getId(), blog));
    }

    @Test
    void whenNonExistingBlogIsDeleted_exceptionIdThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        given(blogRepository.existsById(blogId)).willReturn(false);

        // when
        // then
        assertThrows(BlogNotFoundException.class, () -> blogService.deleteBlog(blogId));
    }

    @Test
    void whenBlogIdDeleted_itsCategoriesAndPublicationsAreDeleted() {

        // given
        BlogId blogId = getDefaultBlogId();
        given(blogRepository.existsById(blogId)).willReturn(true);

        // when
        blogService.deleteBlog(blogId);

        // then
        verify(publicationRepository).deleteAllByBlog_Id(blogId);
        verify(categoryRepository).deleteAllByBlogIdAndUsername(BLOG_ID, USERNAME);
        verify(blogRepository).deleteById(blogId);
    }

    @Test
    void whenGettingNonExistingBlog_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        given(blogRepository.findById(blogId)).willReturn(Optional.empty());

        // when
        // then
        assertThrows(BlogNotFoundException.class, () -> blogService.getById(blogId));
    }

    @Test
    void whenGettingByUsername_returnsProperList() {
        // given
        try (MockedStatic<AuthenticatedUserResolver> resolver = Mockito.mockStatic(AuthenticatedUserResolver.class)) {
            resolver.when(AuthenticatedUserResolver::resolveUsernameFromPrincipal).thenReturn(USERNAME);

            Blog defaultBlog = getDefaultBlog();
            given(blogRepository.findById_Username(USERNAME)).willReturn(List.of(defaultBlog));

            // when
            List<Blog> blogs = blogService.getAllByUsername();

            // then
            assertThat(blogs).containsExactly(defaultBlog);
        }
    }

    @Test
    void whenGettingByUsername_returnsEmptyList() {
        try (MockedStatic<AuthenticatedUserResolver> resolver = Mockito.mockStatic(AuthenticatedUserResolver.class)) {
            resolver.when(AuthenticatedUserResolver::resolveUsernameFromPrincipal).thenReturn(USERNAME);

            given(blogRepository.findById_Username(USERNAME)).willReturn(Collections.emptyList());

            // when
            List<Blog> blogs = blogService.getAllByUsername();

            // then
            assertThat(blogs).isEmpty();
        }
    }
}
