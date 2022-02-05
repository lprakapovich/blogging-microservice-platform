package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Subscription;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, Subscription.SubscriptionId> {

    List<Subscription> getAllById_BlogId(String blogId);

    List<Subscription> getAllById_SubscriberBlogId(String blogId);

    boolean existsById_BlogIdAndId_SubscriberBlogId(String blogId, String subscriberBlogId);
}
