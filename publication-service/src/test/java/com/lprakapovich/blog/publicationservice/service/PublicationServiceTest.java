package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.CategoryNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.PublicationNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Publication;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getDefaultBlog;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getDefaultBlogId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

    @InjectMocks
    private PublicationService publicationService;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private BlogService blogService;

    @Mock
    private CategoryService categoryService;

    @Test
    void whenPublicationIsCreatedWithNonExistingBlog_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        Publication publication = new Publication();
        doThrow(BlogNotFoundException.class).when(blogService).checkExistence(blogId);

        // when
        // then
        assertThrows(BlogNotFoundException.class, () -> publicationService.createPublication(publication, blogId));
        verify(publicationRepository, never()).save(any());
    }

    @Test
    void whenPublicationIsCreatedWithNonExistingCategory_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        Publication publication = new Publication();
        Category category = new Category();
        category.setId(1L);
        publication.setCategory(category);

        doThrow(CategoryNotFoundException.class).when(categoryService).checkExistence(category.getId(), blogId);

        // when
        // then
        assertThrows(CategoryNotFoundException.class, () -> publicationService.createPublication(publication, blogId));
        verify(publicationRepository, never()).save(any());
    }

    @Test
    void whenPublicationIsCreated_BlogIsAssignedCorrectly() {

        // given
        Blog blog = getDefaultBlog();
        Publication publication = new Publication();
        given(blogService.getById(blog.getId())).willReturn(blog);

        // when
        ArgumentCaptor<Publication> publicationCaptor = ArgumentCaptor.forClass(Publication.class);
        publicationService.createPublication(publication, blog.getId());

        // then
        verify(publicationRepository).save(publicationCaptor.capture());
        Publication capturedPublication = publicationCaptor.getValue();
        assertEquals(capturedPublication.getBlog(), blog);
    }

    @Test
    void whenPublicationIsUpdatedForNonExistingBlog_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        Publication publication = new Publication();
        long publicationId = 1L;
        doThrow(BlogNotFoundException.class).when(blogService).checkExistence(blogId);

        // when
        // then
        assertThrows(BlogNotFoundException.class, () -> publicationService.updatePublication(blogId, publicationId, publication));
        verify(publicationRepository, never()).save(any());
    }

    @Test
    void whenNonExistingPublicationIsUpdated_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        Publication publication = new Publication();
        long publicationId = 1L;

        // when
        // then
        assertThrows(PublicationNotFoundException.class, () -> publicationService.updatePublication(blogId, publicationId, publication));
    }

    @Test
    void whenNonExistingPublicationIsDeleted_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        long publicationId = 1L;

        // when
        // then
        assertThrows(PublicationNotFoundException.class, () -> publicationService.deletePublication(publicationId, blogId));
    }
}
