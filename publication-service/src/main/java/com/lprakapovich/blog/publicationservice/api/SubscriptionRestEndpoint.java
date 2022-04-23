package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.SubscriberDto;
import com.lprakapovich.blog.publicationservice.api.dto.SubscriptionDto;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Subscription;
import com.lprakapovich.blog.publicationservice.model.Subscription.SubscriptionId;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.SubscriptionService;
import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
import com.lprakapovich.blog.publicationservice.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publication-service/{blogId},{username}/subscriptions")
@RequiredArgsConstructor
class SubscriptionRestEndpoint {

    private final SubscriptionService subscriptionService;
    private final BlogService blogService;
    private final BlogOwnershipValidator blogOwnershipValidator;

    @PostMapping
    public ResponseEntity<URI> createSubscription(@PathVariable String blogId,
                                                  @PathVariable String username,
                                                  @RequestBody SubscriptionDto subscriptionDto) {

        BlogId subscriberId = new BlogId(blogId, username);
        blogOwnershipValidator.isPrincipalOwner(subscriberId);
        BlogId subscribeToId = subscriptionDto.getSubscription();
        // todo move existence checks to service
        blogService.checkExistence(subscribeToId);

        Subscription subscription = new Subscription(subscriberId, subscribeToId);
        SubscriptionId subscriptionId = subscriptionService.createSubscription(subscription);
        URI uri = UriBuilder.build(
                subscriptionId.getSubscriber().getId(),
                subscriptionId.getSubscriber().getUsername(),
                subscriptionId.getSubscription().getId(),
                subscriptionId.getSubscription().getUsername());

        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{subscriptionBlogId},{subscriptionUsername}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String blogId,
                                                   @PathVariable String username,
                                                   @PathVariable String subscriptionBlogId,
                                                   @PathVariable String subscriptionUsername) {

        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.isPrincipalOwner(id);
        BlogId subscriptionId = new BlogId(subscriptionBlogId, subscriptionUsername);
        subscriptionService.deleteSubscription(new SubscriptionId(id, subscriptionId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> getSubscriptions(@PathVariable String blogId,
                                                                  @PathVariable String username) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.isPrincipalOwner(id);
        List<Subscription> subscriptions = subscriptionService.getAllBlogSubscriptions(id);
        List<SubscriptionDto> subscriberDtos = subscriptions
                .stream()
                .map(subscription -> new SubscriptionDto(
                        subscription.getId().getSubscription()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriberDtos);
    }

    @GetMapping("/subscribers")
    public ResponseEntity<List<SubscriberDto>> getSubscribers(@PathVariable String blogId,
                                                              @PathVariable String username) {
        BlogId id = new BlogId(blogId, username);
        blogOwnershipValidator.isPrincipalOwner(id);
        List<Subscription> subscriptions = subscriptionService.getAllBlogSubscribers(id);
        List<SubscriberDto> subscriberDtos = subscriptions.stream()
                .map(subscription -> new SubscriberDto(
                        subscription.getId().getSubscriber()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriberDtos);
    }
}
