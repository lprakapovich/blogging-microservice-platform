package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.InvalidSubscriptionException;
import com.lprakapovich.blog.publicationservice.exception.SubscriptionNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import com.lprakapovich.blog.publicationservice.model.Subscription;
import com.lprakapovich.blog.publicationservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver.resolveUsernameFromPrincipal;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription.SubscriptionId createSubscription(Subscription subscription) {
        checkSubscriptionTarget(subscription);
        Subscription createdSubscription = subscriptionRepository.save(subscription);
        return createdSubscription.getId();
    }

    public void deleteSubscription(Subscription.SubscriptionId id) {
        checkExistence(id);
        subscriptionRepository.deleteById(id);
    }

    public List<Subscription> getAllBlogSubscriptions(BlogId blogId) {
        return subscriptionRepository.getAllById_Subscriber(blogId);
    }

    public List<Subscription> getAllBlogSubscribers(BlogId blogId) {
        return subscriptionRepository.getAllById_Subscription(blogId);
    }

    public void checkExistence(Subscription.SubscriptionId id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new SubscriptionNotFoundException();
        }
    }

    private void checkSubscriptionTarget(Subscription subscription) {
        String authenticatedUser = resolveUsernameFromPrincipal();
        if (authenticatedUser.equals(subscription.getId().getSubscription().getUsername())) {
            throw new InvalidSubscriptionException();
        }
    }
}
