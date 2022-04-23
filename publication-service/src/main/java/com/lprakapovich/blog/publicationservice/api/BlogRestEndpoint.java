package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.BlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.BlogViewDto;
import com.lprakapovich.blog.publicationservice.api.dto.CreateBlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.UpdateBlogDto;
import com.lprakapovich.blog.publicationservice.api.dto.utils.GenericMappingUtils;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Category;
import com.lprakapovich.blog.publicationservice.model.Subscription;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.CategoryService;
import com.lprakapovich.blog.publicationservice.service.SubscriptionService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import com.lprakapovich.blog.publicationservice.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolveUsernameFromPrincipal;

@Controller
@RequestMapping("/publication-service/blogs")
@RequiredArgsConstructor
class BlogRestEndpoint {

    private final BlogService blogService;
    private final CategoryService categoryService;
    private final SubscriptionService subscriptionService;
    private final BlogOwnershipValidator blogOwnershipValidator;
    private final GenericMappingUtils mappingUtils;


    @PostMapping
    public ResponseEntity<URI> createBlog(@RequestBody @Valid CreateBlogDto blogDto) {
        BlogId blogId = new BlogId(blogDto.getId(), resolveUsernameFromPrincipal());
        Blog blog = Blog.builder().id(blogId).build();
        BlogId created = blogService.createBlog(blog);
        URI uri = UriBuilder.build(created.getId(), created.getUsername());
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id},{username}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable String id,
                                              @PathVariable String username,
                                              @RequestBody @Valid UpdateBlogDto blogDto) {
        BlogId blogId = new BlogId(id, username);
        blogOwnershipValidator.isPrincipalOwner(blogId);
        Blog updated = Blog.builder()
                .description(blogDto.getDescription())
                .displayName(blogDto.getDisplayName())
                .build();
        Blog updatedBlog = blogService.updateBlog(blogId, updated);
        return ResponseEntity.ok(mappingUtils.map(updatedBlog, BlogDto.class));
    }

    @DeleteMapping("/{id},{username}")
    public ResponseEntity<Void> deleteBlog(@PathVariable String id,
                                           @PathVariable String username) {

        BlogId blogId = new BlogId(id, username);
        blogOwnershipValidator.isPrincipalOwner(blogId);
        blogService.deleteBlog(blogId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogDto>> getBlogsBySearchCriteria(@RequestParam(required = false) String criteria) {
        List<Blog> blogs = blogService.getAllBySearchCriteria(criteria);
        return ResponseEntity.ok(mappingUtils.mapList(blogs, BlogDto.class));
    }

    @GetMapping("/owned")
    public ResponseEntity<List<BlogViewDto>> getUserBlogViews() {
        List<BlogViewDto> blogViews = blogService.getAllByUsername()
                .stream()
                .map(this::buildBlogView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(blogViews);
    }

    @GetMapping("/{id},{username}")
    public ResponseEntity<BlogViewDto> getBlogView(@PathVariable String id,
                                                   @PathVariable String username) {
        BlogId blogId = new BlogId(id, username);
        return ResponseEntity.ok(buildBlogView(blogService.getById(blogId)));
    }

    @PostMapping("/check")
    public ResponseEntity<Void> checkExistence(@RequestParam String blogId) {
        HttpStatus responseStatus =  blogService.existsById(blogId) ? HttpStatus.CONFLICT : HttpStatus.OK;
        return ResponseEntity.status(responseStatus).build();
    }

    private BlogViewDto buildBlogView(Blog blog) {
        BlogId blogId = blog.getId();
        List<Category> categories = categoryService.getByBlogId(blogId);
        List<Subscription> subscribers = subscriptionService.getAllBlogSubscribers(blogId);
        List<Subscription> subscriptions = subscriptionService.getAllBlogSubscriptions(blogId);
        return new BlogViewDto(
                blog.getId(),
                blog.getDisplayName(),
                blog.getDescription(),
                categories,
                subscriptions,
                subscribers
        );
    }
}
