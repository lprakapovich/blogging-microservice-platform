package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CategoryDto;
import com.lprakapovich.blog.publicationservice.api.dto.CreateCategoryDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateCategoryDto;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveUsernameFromPrincipal;

@Controller
@RequestMapping("/publication-service/uri/{blogId}/categories")
@RequiredArgsConstructor
class CategoryRestEndpoint {

    private final BlogService blogService;
    private final CategoryService categoryService;
    private final ObjectMapper mapper;

    @PostMapping
    public ResponseEntity<URI> createCategory(@PathVariable String blogId,
                                              @Valid @RequestBody CreateCategoryDto categoryDto) {
        checkBlog(blogId);
        long categoryId = categoryService.createCategory(new Category(categoryDto.getName()), blogId);
        return ResponseEntity.created(URI.create(String.valueOf(categoryId))).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable String blogId,
                                                      @PathVariable long id,
                                                      @Valid @RequestBody UpdateCategoryDto categoryDto) {
        checkBlog(blogId);
        Category updatedCategory = mapper.convertValue(categoryDto, Category.class);
        Category updated = categoryService.updateCategory(id, updatedCategory, blogId);
        return ResponseEntity.accepted().body(map(updated));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@PathVariable String blogId) {
        checkBlog(blogId);
        List<Category> allByBlogId = categoryService.getAllByBlogId(blogId);
        return ResponseEntity.ok(map(allByBlogId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable String blogId, @PathVariable long id) {
        checkBlog(blogId);
        Category byId = categoryService.getById(id, blogId);
        return ResponseEntity.ok(map(byId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String blogId, @PathVariable long id) {
        checkBlog(blogId);
        categoryService.deleteCategory(id, blogId);
        return ResponseEntity.noContent().build();
    }

    private void checkBlog(String blogId) {
        String authenticatedUsername = resolveUsernameFromPrincipal();
        blogService.validateExistence(blogId, authenticatedUsername);
    }

    private List<CategoryDto> map(List<Category> publications) {
        return publications
                .stream()
                .map(p -> mapper.convertValue(p, CategoryDto.class))
                .collect(Collectors.toList());
    }

    private CategoryDto map(Category category) {
        return mapper.convertValue(category, CategoryDto.class);
    }
}
