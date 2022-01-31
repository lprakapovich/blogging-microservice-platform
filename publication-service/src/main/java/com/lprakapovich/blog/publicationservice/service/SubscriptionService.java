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

    public Subscription.SubscriptionId saveSubscription(Subscription subscription) {
        Subscription save = subscriptionRepository.save(subscription);
        return save.getId();
    }

    public void deleteSubscription(Subscription subscription) {
        if (!subscriptionRepository.existsById(subscription.getId())) {
            throw new SubscriptionNotFoundException();
        }
        subscriptionRepository.deleteById(subscription.getId());
    }

    public List<Subscription> getAllSubscriptions(String blogId) {
        return subscriptionRepository.getAllById_SubscriberBlogId(blogId);
    }

    public List<Subscription> getAllSubscribers(String blogId) {
        return subscriptionRepository.getAllById_BlogId(blogId);
    }
}
