package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.BlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.BlogViewDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateBlogDto;
import com.lprakapovich.blog.publicationservice.exception.BlogIdMismatchException;
import com.lprakapovich.blog.publicationservice.model.*;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.service.PublicationService;
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

    @GetMapping
    public ResponseEntity<List<Blog>> getAll() {
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
                blog.getDescription(),
                blog.getAuthor(),
                categories,
                subscriptions.size(),
                subscribers.size()
        );
        return ResponseEntity.ok(blogViewDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable String id, @Valid @RequestBody UpdateBlogDto blogDto) {
        String blogId = resolveBlogId(id);
        blogService.updateBlogDescription(blogId, blogDto.getDescription());
        return ResponseEntity.ok().build();
    }

    private String resolveBlogId(String id) {
        String blogId = resolveBlogIdFromPrincipal();
        blogService.validateExistence(blogId);
        if (!id.equals(blogId)) {
            throw new BlogIdMismatchException();
        }
        return blogId;
    }
}
