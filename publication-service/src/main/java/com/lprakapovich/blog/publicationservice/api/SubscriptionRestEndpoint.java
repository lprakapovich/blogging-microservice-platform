package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.api.dto.SubscriberDto;
import com.lprakapovich.blog.publicationservice.api.dto.SubscriptionDto;
import com.lprakapovich.blog.publicationservice.model.Subscription;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import com.lprakapovich.blog.publicationservice.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.lprakapovich.blog.publicationservice.util.BlogIdResolver.resolveUsernameFromPrincipal;

@RestController
@RequestMapping("/publication-service/uri/{blogId}/subscriptions")
@RequiredArgsConstructor
class SubscriptionRestEndpoint {

    private final SubscriptionService subscriptionService;
    private final BlogService blogService;

    @PostMapping
    public ResponseEntity<URI> createSubscription(@PathVariable String blogId,
                                                  @Valid @RequestBody SubscriptionDto subscriptionDto) {
        checkBlog(blogId);
        String subscribeToId = subscriptionDto.getSubscribeToId();
        blogService.validateExistence(subscribeToId);
        Subscription subscription = new Subscription(subscribeToId, blogId);
        Subscription.SubscriptionId subscriptionId = subscriptionService.createSubscription(subscription);
        URI uri = URI.create(String.join(",", subscriptionId.getBlogId(), subscriptionId.getSubscriberBlogId()));
        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionDto>> getSubscriptions(@PathVariable String blogId) {
        checkBlog(blogId);
        List<Subscription> subscriptions = subscriptionService.getAllBlogSubscriptions(blogId);
        List<SubscriptionDto> subscriberDtos = subscriptions
                .stream()
                .map(subscription -> new SubscriptionDto(subscription.getId().getBlogId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriberDtos);
    }

    @GetMapping("/subscribers")
    public ResponseEntity<List<SubscriberDto>> getSubscribers(@PathVariable String blogId) {
        checkBlog(blogId);
        List<Subscription> subscriptions = subscriptionService.getAllBlogSubscribers(blogId);
        List<SubscriberDto> subscriberDtos = subscriptions.stream()
                .map(subscription -> new SubscriberDto(subscription.getId().getSubscriberBlogId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(subscriberDtos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscription(@PathVariable String blogId,
                                                   @PathVariable(value = "id") String blogIdToUnsubscribe) {
        checkBlog(blogId);
        subscriptionService.deleteSubscription(new Subscription.SubscriptionId(blogIdToUnsubscribe, blogId));
        return ResponseEntity.noContent().build();
    }


    private void checkBlog(String blogId) {
        String authenticatedUsername = resolveUsernameFromPrincipal();
        blogService.validateExistence(blogId, authenticatedUsername);
    }
}
