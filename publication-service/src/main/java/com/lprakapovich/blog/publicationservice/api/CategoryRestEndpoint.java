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

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveBlogIdFromPrincipal;

@Controller
@RequestMapping("/publication-service/categories")
@RequiredArgsConstructor
class CategoryRestEndpoint {

    private final BlogService blogService;
    private final CategoryService categoryService;
    private final ObjectMapper mapper;

    @PostMapping
    public ResponseEntity<URI> createCategory(@Valid @RequestBody CreateCategoryDto categoryDto) {
        String blogId = resolveBlogId();
        long categoryId = categoryService.createCategory(new Category(categoryDto.getName()), blogId);
        return ResponseEntity.created(URI.create(String.valueOf(categoryId))).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long id, @Valid @RequestBody UpdateCategoryDto categoryDto) {
        String blogId = resolveBlogId();
        Category updatedCategory = mapper.convertValue(categoryDto, Category.class);
        Category updated = categoryService.updateCategory(id, updatedCategory, blogId);
        return ResponseEntity.accepted().body(map(updated));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories() {
        List<Category> allByBlogId = categoryService.getAllByBlogId(resolveBlogId());
        return ResponseEntity.ok(map(allByBlogId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable long id) {
        String blogId =  resolveBlogId();
        Category byId = categoryService.getById(id, blogId);
        return ResponseEntity.ok(map(byId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        String blogId = resolveBlogId();
        categoryService.deleteCategory(id, blogId);
        return ResponseEntity.noContent().build();
    }

    private String resolveBlogId() {
        String blogId = resolveBlogIdFromPrincipal();
        blogService.validateExistence(blogId);
        return blogId;
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
