package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.DuplicatedCategoryNameException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.repository.BlogRepository;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryServiceTest {

    private final String BLOG_ID = "id";
    private final String CATEGORY_NAME = "name";

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private BlogService blogService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BlogRepository blogRepository;

    @Captor
    private ArgumentCaptor<Category> categoryArgumentCaptor;

    @Test
    public void whenCategoryIsCreated_thenNameUniquenessIsValidated() {
        Category category = new Category(CATEGORY_NAME);
        Blog blog = new Blog();
        when(blogService.getById(BLOG_ID)).thenReturn(blog);
        when(categoryRepository.existsByNameAndBlogId(CATEGORY_NAME, BLOG_ID)).thenReturn(false);
        given(categoryRepository.save(category)).willAnswer(invocation -> invocation.getArgument(0));

        Long createdCategoryId = categoryService.createCategory(category, BLOG_ID);

        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());
        assertThat(category).isEqualTo(categoryArgumentCaptor.getValue());
        assertThat(createdCategoryId).isNotNull();
    }

    @Test
    public void whenCategoryIsCreatedWithDuplicatedName_thenExceptionIsThrown() {
        Category category = new Category(CATEGORY_NAME);
        Blog blog = new Blog();

        when(blogService.getById(BLOG_ID)).thenReturn(blog);
        when(categoryRepository.existsByNameAndBlogId(CATEGORY_NAME, BLOG_ID)).thenReturn(true);

        assertThrows(DuplicatedCategoryNameException.class, () -> categoryService.createCategory(category, BLOG_ID));
        verify(categoryRepository, never()).save(category);
    }

    @Test
    public void whenCategoryIsCreatedForNonExistingBlog_thenExceptionIsThrown() {

    }

    @Test
    public void whenCategoryNameIsSetToNull_thenErrorExceptionIsThrown() {}

    @Test
    public void whenNonExistingCategoryIsUpdated_thenExceptionIsThrown() {}

    @Test
    public void whenNonExistingCategoryIdFetched_thenExceptionIsThrown() {}
}
