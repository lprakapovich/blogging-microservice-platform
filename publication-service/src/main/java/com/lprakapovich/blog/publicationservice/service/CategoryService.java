package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.CategoryNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final BlogService blogService;
    private final CategoryRepository categoryRepository;

    public Category getById(long id) {
        return categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }

    @Transactional
    public long createCategory(Category category, String blogId) {
        Blog blog = blogService.getById(blogId);
        Category save = categoryRepository.save(category);
        blog.getCategories().add(save);
        return save.getId();
    }

    // TODO set to null all publications with this category
    @Transactional
    public long deleteCategory(long categoryId, String blogId) {
        validateExistence(categoryId);
        categoryRepository.deleteById(categoryId);
        return categoryId;
    }

    @Transactional
    public Category updateCategory(long categoryId, Category updatedCategory, String blogId) {
        categoryRepository.updateCategoryName(updatedCategory.getName(), categoryId, blogId);
        return getById(categoryId);
    }

    public void validateExistence(long id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException();
        }
    }
}
