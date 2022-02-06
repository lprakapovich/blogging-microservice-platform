package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.CategoryNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BlogService blogService;
    private final PublicationRepository publicationRepository;
    private final CategoryRepository categoryRepository;

    public long createCategory(Category category, String blogId) {
        Blog blog = blogService.getById(blogId);
        Category createdCategory = categoryRepository.save(category);
        blog.getCategories().add(createdCategory);
        return createdCategory.getId();
    }

    public Category updateCategory(long categoryId, Category updatedCategory, String blogId) {
        categoryRepository.updateCategoryName(updatedCategory.getName(), categoryId, blogId);
        return getById(categoryId, blogId);
    }

    public List<Category> getAllByBlogId(String blogId) {
        return categoryRepository.findByBlogId(blogId);
    }

    public Category getById(long id, String blogId) {
        return categoryRepository.findByIdAndBlogId(id, blogId).orElseThrow(CategoryNotFoundException::new);
    }

    public void deleteCategory(long categoryId, String blogId) {
        checkCategory(categoryId, blogId);
        publicationRepository.findByCategory_IdAndBlog_Id(categoryId, blogId)
                .forEach(publication -> publication.setCategory(null));
        categoryRepository.deleteById(categoryId);
    }

    public void checkCategory(long id, String blogId) {
        if (categoryRepository.findByIdAndBlogId(id, blogId).isEmpty()) {
            throw new CategoryNotFoundException();
        }
    }
}
