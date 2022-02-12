package com.lprakapovich.blog.publicationservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.blog.publicationservice.api.dto.BlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.BlogViewDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateBlogDto;
import com.lprakapovich.blog.publicationservice.exception.BlogIdMismatchException;
import com.lprakapovich.blog.publicationservice.model.*;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.service.SubscriptionService;
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
    private final SubscriptionService subscriptionService;
    private final CategoryService categoryService;
    private final ObjectMapper mapper;

    @PutMapping("/{id}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable String id, @RequestBody UpdateBlogDto blogDto) {
        String blogId = resolveBlogId(id);
        Blog blog = blogService.updateBlog(blogId, blogDto.getName(), blogDto.getDescription());
        return ResponseEntity.ok(map(blog));
    }

    @GetMapping
    public ResponseEntity<List<Blog>> getBlogs() {
        return ResponseEntity.ok(blogService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogViewDto> getBlogView(@PathVariable String id) {
        blogService.validateExistence(id);
        Blog blog = blogService.getById(id);
        List<Subscription> subscriptions = subscriptionService.getAllBlogSubscriptions(id);
        List<Subscription> subscribers = subscriptionService.getAllBlogSubscribers(id);
        List<Category> categories = categoryService.getAllByBlogId(id);
        BlogViewDto blogViewDto = new BlogViewDto(
                id,
                blog.getName(),
                blog.getDescription(),
                categories,
                subscriptions.size(),
                subscribers.size()
        );
        return ResponseEntity.ok(blogViewDto);
    }

    private String resolveBlogId(String id) {
        String blogId = resolveBlogIdFromPrincipal();
        blogService.validateExistence(blogId);
        if (!id.equals(blogId)) {
            throw new BlogIdMismatchException();
        }
        return blogId;
    }

    private BlogDto map(Blog blog) {
        return mapper.convertValue(blog, BlogDto.class);
    }
}
