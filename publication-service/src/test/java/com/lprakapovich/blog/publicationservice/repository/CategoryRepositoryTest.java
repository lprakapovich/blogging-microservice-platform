package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.exception.BlogNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.BlogUtil.BLOG_ID;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 *  JPA Repositories with extensive use of native SQL queries must be tested
 */

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BlogRepository blogRepository;

    public static final String DEFAULT_NAME = "name";

    @Test
    void whenCategoryIsSavedWithMissingName_exceptionIsThrown() {

        // given
        createBlogDefaultInstance();
        Category category = new Category();
        category.setBlog(getBlogDefaultInstance());

        // when
        // then
        assertThrows(ConstraintViolationException.class, () -> categoryRepository.save(category));
    }

    @Test
    void whenCategoryIsSavedWithMissingBlog_exceptionIsThrown() {

        // given
        Category category = new Category();
        category.setName(DEFAULT_NAME);

        // when
        // then
        assertThrows(ConstraintViolationException.class, () -> categoryRepository.save(category));
    }

    @Test
    void whenValidCategoryIsSaved_exceptionNotThrown() {

        // given
        createBlogDefaultInstance();
        Category category = getCategoryDefaultInstance();


        // when
        // then
        assertDoesNotThrow(() -> categoryRepository.save(category));
    }

    @Test
    void whenExistsByNameAndBlogId_ReturnsTrue() {

        // given
        createBlogDefaultInstance();
        Category category = getCategoryDefaultInstance();

        // when
        categoryRepository.save(category);

        // then
        assertTrue(categoryRepository.existsByNameAndBlogId(DEFAULT_NAME, BLOG_ID, USERNAME));
    }

    @Test
    void whenDoesNotExistByNameAndBlogId_ReturnsFalse() {

        // given
        // when
        // then
        assertFalse(categoryRepository.existsByNameAndBlogId(DEFAULT_NAME, BLOG_ID, USERNAME));
    }

    @Test
    void whenCategoriesDoNotExist_returnsEmptyList() {

        // given
        createBlogDefaultInstance();

        // when
        // then
        assertThat(categoryRepository.findByBlogId(BLOG_ID, USERNAME)).isEmpty();
    }

    @Test
    void whenCategoriesExists_returnsProperResult() {

        // given
        createBlogDefaultInstance();
        Category category1 = getCategoryInstance("name1");
        Category category2 = getCategoryInstance("name2");
        categoryRepository.saveAll(List.of(category1, category2));

        // when
        // then
        List<Category> categories = categoryRepository.findByBlogId(BLOG_ID, USERNAME);
        assertThat(categories)
                .isNotEmpty()
                .containsExactly(category1, category2);
    }

    private Category getCategoryDefaultInstance() {
        Category category = new Category();
        category.setName(DEFAULT_NAME);
        category.setBlog(getBlogDefaultInstance());
        return category;
    }

    private Category getCategoryInstance(String name) {
        Category category = new Category();
        category.setName(name);
        category.setBlog(getBlogDefaultInstance());
        return category;
    }

    private void createBlogDefaultInstance() {
        Blog blog = new Blog();
        blog.setId(new Blog.BlogId(BLOG_ID, USERNAME));
        blogRepository.save(blog);
    }

    private Blog getBlogDefaultInstance() {
        return blogRepository.findById(new Blog.BlogId(BLOG_ID, USERNAME))
                .orElseThrow(BlogNotFoundException::new);
    }
}
