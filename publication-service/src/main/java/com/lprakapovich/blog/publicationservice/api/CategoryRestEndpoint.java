package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.CategoryDto;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Content;
import com.lprakapovich.blog.publicationservice.model.Status;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveBlogIdFromPrincipal;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
class CategoryRestEndpoint {

    private final BlogService blogService;
    private final CategoryService categoryService;
    private final ObjectMapper mapper;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll() {
        List<Category> allByBlogId = categoryService.getAllByBlogId(resolveBlogId());
        return ResponseEntity.ok(map(allByBlogId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable long id) {
        String blogId =  resolveBlogId();
        Category byId = categoryService.getById(id, blogId);
        return ResponseEntity.ok(map(byId));
    }

    @PostMapping
    public ResponseEntity<URI> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        String blogId = resolveBlogId();
        long categoryId = categoryService.createCategory(new Category(categoryDto.getName()), blogId);
        return ResponseEntity.created(URI.create(String.valueOf(categoryId))).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
        String blogId = resolveBlogId();
        categoryService.deleteCategory(id, blogId);
        return ResponseEntity.noContent().build();
    }

    // TODO refactor to use DTO
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable long id, @Valid @RequestBody CategoryDto categoryDto) {
        String blogId = resolveBlogId();
        Category updatedCategory = mapToEntity(categoryDto);
        Category updated = categoryService.updateCategory(id, updatedCategory, blogId);
        return ResponseEntity.accepted().body(map(updated));
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

    private Category mapToEntity(CategoryDto categoryDto) {
        return mapper.convertValue(categoryDto, Category.class);
    }

    @Data
    public static class CreatePublicationDto {

        @NotBlank(message = "Publication header cannot be blank")
        private String header;

        private String subHeader;

        private Category category;

        @NotNull(message = "Publication must have an assigned status - Published, Draft or Hidden")
        private Status status;

        @NotBlank(message = "Publication content cannot me blank")
        private Content content;
    }
}
