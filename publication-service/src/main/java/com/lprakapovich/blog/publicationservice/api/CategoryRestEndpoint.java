package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.CreateCategoryDto;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/publication-service/{blogId},{username}/categories")
@RequiredArgsConstructor
class CategoryRestEndpoint {

    private final CategoryService categoryService;
    private final BlogOwnershipValidator blogOwnershipValidator;

    @PostMapping
    public ResponseEntity<Long> createCategory(@PathVariable String blogId,
                                              @PathVariable String username,
                                              @Valid @RequestBody CreateCategoryDto categoryDto) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.isPrincipalOwner(id);
        long createdCategoryId = categoryService.createCategory(id, categoryDto.getName());
        return ResponseEntity.ok().body(createdCategoryId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String blogId,
                                               @PathVariable String username,
                                               @PathVariable(name = "id") long categoryId) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.isPrincipalOwner(id);
        categoryService.deleteCategory(id, categoryId);
        return ResponseEntity.noContent().build();
    }
}
