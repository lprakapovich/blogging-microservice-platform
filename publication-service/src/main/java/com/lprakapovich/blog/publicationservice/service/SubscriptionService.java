package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.SubscriptionNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Subscription;
import com.lprakapovich.blog.publicationservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public Subscription.SubscriptionId createSubscription(Subscription subscription) {
        Subscription createdSubscription = subscriptionRepository.save(subscription);
        return createdSubscription.getId();
    }

    public void deleteSubscription(Subscription subscription) {
        checkSubscription(subscription.getId());
        subscriptionRepository.deleteById(subscription.getId());
    }

    public List<Subscription> getAllBlogSubscriptions(String blogId) {
        return subscriptionRepository.getAllById_SubscriberBlogId(blogId);
    }

    public List<Subscription> getAllBlogSubscribers(String blogId) {
        return subscriptionRepository.getAllById_BlogId(blogId);
    }

    public void checkSubscription(String blogId, String subscriberBlogId) {
        if (!subscriptionRepository.existsById_BlogIdAndId_SubscriberBlogId(blogId, subscriberBlogId)) {
            throw new SubscriptionNotFoundException();
        }
    }

    private void checkSubscription(Subscription.SubscriptionId id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new SubscriptionNotFoundException();
        }
    }
}
