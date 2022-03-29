package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.model.Subscription;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, Subscription.SubscriptionId> {

    List<Subscription> getAllById_Subscriber(Blog.BlogId blogId);

    List<Subscription> getAllById_Subscription(Blog.BlogId blogId);
}
