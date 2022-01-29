package com.lprakapovich.blog.publicationservice.repository;

import com.lprakapovich.blog.publicationservice.domain.Subscription;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, Subscription.SubscriptionId> {
}
