package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CategoryDto;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveBlogIdFromPrincipal;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
class CategoryRestEndpoint {

    private final BlogService blogService;
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<URI> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        String blogId = resolveBlogId();
        long categoryId = categoryService.createCategory(new Category(categoryDto.getName()), blogId);
        return ResponseEntity.created(URI.create(String.valueOf(categoryId))).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteCategory(@PathVariable long id) {
        String blogId = resolveBlogId();
        long deletedResourceId = categoryService.deleteCategory(id, blogId);
        return ResponseEntity.ok(deletedResourceId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryDto categoryDto) {
        String blogId = resolveBlogId();
        Category updatedCategory = new Category(categoryDto.getName());
        Category updated = categoryService.updateCategory(id, updatedCategory, blogId);
        return ResponseEntity.accepted().body(updated);
    }

    private String resolveBlogId() {
        String blogId = resolveBlogIdFromPrincipal();
        blogService.validateExistence(blogId);
        return blogId;
    }
}
