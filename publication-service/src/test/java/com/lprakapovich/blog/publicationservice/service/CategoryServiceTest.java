package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.CategoryNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.DuplicatedCategoryNameException;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getDefaultBlog;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getDefaultBlogId;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private BlogService blogService;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void whenCategoryForNonExistingBlogIsCreated_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        given(blogService.getById(blogId)).willThrow(BlogNotFoundException.class);

        // when
        // then
        assertThrows(BlogNotFoundException.class, () -> categoryService.createCategory(blogId, "name"));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void whenCategoryWithNonUniqueNamePerBlogIsCreated_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        String name = "name";
        given(blogService.getById(blogId)).willReturn(getDefaultBlog());
        given(categoryRepository.existsByNameAndBlogId(name, blogId.getId(), blogId.getUsername())).willReturn(true);

        // when
        // then
        assertThrows(DuplicatedCategoryNameException.class, () -> categoryService.createCategory(blogId, "name"));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void whenCategoryForNonExistingBlogIsDeleted_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        doThrow(BlogNotFoundException.class).when(blogService).checkExistence(blogId);

        // when
        // then
        assertThrows(BlogNotFoundException.class, () -> categoryService.deleteCategory(blogId, 1L));
        verify(categoryRepository, never()).deleteById(1L);
    }

    @Test
    void whenNonExistingCategoryIsDeleted_exceptionIsThrown() {

        // given
        BlogId blogId = getDefaultBlogId();
        given(categoryRepository.findByIdAndBlogId(anyLong(), eq(blogId.getId()), eq(blogId.getUsername())))
                .willReturn(Optional.empty());

        // when
        // then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.deleteCategory(blogId, 1L));
    }

    @Test
    void whenCategoryIsDeleted_allAssignedPublicationsAreUpdated() {

        // given
        BlogId blogId = getDefaultBlogId();
        long categoryId = 1L;
        given(categoryRepository.findByIdAndBlogId(categoryId, blogId.getId(), blogId.getUsername()))
                .willReturn(Optional.of(new Category()));

        // when
        categoryService.deleteCategory(blogId, categoryId);

        // then
        verify(publicationRepository).findByCategory_IdAndBlog_Id(categoryId, blogId);
        verify(categoryRepository).deleteById(categoryId);
    }
}
