package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.CategoryNotFoundException;
import com.lprakapovich.blog.publicationservice.exception.DuplicatedCategoryNameException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BlogService blogService;
    private final PublicationRepository publicationRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public long createCategory(BlogId id, String categoryName) {
        Blog blog = blogService.getById(id);
        checkNameUniquenessPerBlog(categoryName, id);
        Category category = new Category();
        category.setName(categoryName);
        category.setBlog(blog);
        Category created = categoryRepository.save(category);
        return created.getId();
    }

    @Transactional
    public void deleteCategory(BlogId blogId, long categoryId) {
        blogService.checkExistence(blogId);
        checkExistence(categoryId, blogId);
        publicationRepository
                .findByCategory_IdAndBlog_Id(categoryId, blogId)
                .forEach(publication -> publication.setCategory(null));
        categoryRepository.deleteById(categoryId);
    }

    public Category getById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public List<Category> getByBlogId(BlogId blogId) {
        return categoryRepository.findByBlogId(blogId.getId(), blogId.getUsername());
    }

    public void checkExistence(long id, BlogId blogId) {
        if (categoryRepository.findByIdAndBlogId(id, blogId.getId(), blogId.getUsername()).isEmpty()) {
            throw new CategoryNotFoundException();
        }
    }

    public void checkNameUniquenessPerBlog(String name, BlogId blogId) {
        if (categoryRepository.existsByNameAndBlogId(name, blogId.getId(), blogId.getUsername())) {
            throw new DuplicatedCategoryNameException();
        }
    }
}
