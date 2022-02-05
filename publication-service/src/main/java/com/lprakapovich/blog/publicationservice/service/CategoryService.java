package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.CategoryNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog;
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

    public List<Category> getAllByBlogId(String blogId) {
        return categoryRepository.findByBlogId(blogId);
    }

    public Category getById(long id, String blogId) {
        return categoryRepository.findByIdAndBlogId(id, blogId).orElseThrow(CategoryNotFoundException::new);
    }

    @Transactional
    public long createCategory(Category category, String blogId) {
        Blog blog = blogService.getById(blogId);
        Category save = categoryRepository.save(category);
        blog.getCategories().add(save);
        return save.getId();
    }

    // todo:  set to null all publications with this category
    @Transactional
    public void deleteCategory(long categoryId, String blogId) {
        validateExistence(categoryId, blogId);
        publicationRepository.findByCategory_IdAndBlog_Id(categoryId, blogId)
                .forEach(publication -> publication.setCategory(null));
        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public Category updateCategory(long categoryId, Category updatedCategory, String blogId) {
        categoryRepository.updateCategoryName(updatedCategory.getName(), categoryId, blogId);
        return getById(categoryId, blogId);
    }

    public void validateExistence(long id, String blogId) {
        if (categoryRepository.findByIdAndBlogId(id, blogId).isEmpty()) {
            throw new CategoryNotFoundException();
        }
    }
}
