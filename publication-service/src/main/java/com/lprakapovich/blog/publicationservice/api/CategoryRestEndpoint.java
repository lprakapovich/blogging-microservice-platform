package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CategoryDto;
import com.lprakapovich.blog.publicationservice.api.dto.CreateCategoryDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateCategoryDto;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/publication-service/{blogId},{username}/categories")
@RequiredArgsConstructor
class CategoryRestEndpoint {

    private final CategoryService categoryService;
    private final BlogOwnershipValidator blogOwnershipValidator;
    private final ObjectMapper mapper;

    @PostMapping
    public ResponseEntity<URI> createCategory(@PathVariable String blogId,
                                              @PathVariable String username,
                                              @Valid @RequestBody CreateCategoryDto categoryDto) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        long createdCategoryId = categoryService.createCategory(id, categoryDto.getName());
        return ResponseEntity.created(URI.create(String.valueOf(createdCategoryId))).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String blogId,
                                                      @PathVariable String username,
                                                      @PathVariable(name = "id") long categoryId,
                                                      @Valid @RequestBody UpdateCategoryDto categoryDto) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        categoryService.updateCategory(id, categoryId, categoryDto.getName());
        Category updated = categoryService.getById(categoryId);
        return ResponseEntity.accepted().body(map(updated));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@PathVariable String blogId,
                                                           @PathVariable String username) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        List<Category> categories = categoryService.getByBlogId(id);
        return ResponseEntity.ok(map(categories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable String blogId,
                                                   @PathVariable String username,
                                                   @PathVariable(name = "id") long categoryId) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        Category byId = categoryService.getByIdAndBlogId(categoryId, id);
        return ResponseEntity.ok(map(byId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String blogId,
                                               @PathVariable String username,
                                               @PathVariable(name = "id") long categoryId) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.validate(id);
        categoryService.deleteCategory(id, categoryId);
        return ResponseEntity.noContent().build();
    }

    private List<CategoryDto> map(List<Category> categories) {
        return categories
                .stream()
                .map(c -> mapper.convertValue(c, CategoryDto.class))
                .collect(Collectors.toList());
    }

    private CategoryDto map(Category category) {
        return mapper.convertValue(category, CategoryDto.class);
    }
}
