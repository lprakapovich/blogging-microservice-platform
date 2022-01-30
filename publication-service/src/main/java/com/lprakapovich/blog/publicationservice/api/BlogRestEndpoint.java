package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.model.Author;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/blogs")
@RequiredArgsConstructor
class BlogRestEndpoint {

    private final BlogService blogService;

    @GetMapping
    public ResponseEntity<List<Blog>> getAll() {
        return ResponseEntity.ok(blogService.getAll());
    }

    @PostMapping
    public ResponseEntity<URI> createBlog(@RequestParam String username) {
        Blog blog = new Blog(username, "description", Instant.now(), new Author("L", "P"), new ArrayList<>());
        String blogId = blogService.createBlog(blog);
        return ResponseEntity.created(URI.create(blogId)).build();
    }
}
