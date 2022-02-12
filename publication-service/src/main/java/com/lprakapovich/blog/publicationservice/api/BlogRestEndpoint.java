package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.BlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateBlogDto;
import com.lprakapovich.blog.publicationservice.exception.BlogIdMismatchException;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveBlogIdFromPrincipal;

@Controller
@RequestMapping("/publication-service/blogs")
@RequiredArgsConstructor
class BlogRestEndpoint {

    private final BlogService blogService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Blog>> getAll() {
        return ResponseEntity.ok(blogService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable String id, @Valid @RequestBody UpdateBlogDto blogDto) {
        String blogId = resolveBlogId();
        if (!id.equals(blogId)) {
            throw new BlogIdMismatchException();
        }

        blogService.updateBlogDescription(id, blogDto.getDescription());
        return ResponseEntity.ok().build();
    }

    private String resolveBlogId() {
        String blogId = resolveBlogIdFromPrincipal();
        blogService.validateExistence(blogId);
        return blogId;
    }
}
