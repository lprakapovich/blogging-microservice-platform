package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.exception.InvalidSubscriptionException;
import com.lprakapovich.blog.publicationservice.exception.SubscriptionNotFoundException;
import com.lprakapovich.blog.publicationservice.model.Subscription;
import com.lprakapovich.blog.publicationservice.model.Subscription.SubscriptionId;
import com.lprakapovich.blog.publicationservice.repository.SubscriptionRepository;
import com.lprakapovich.blog.publicationservice.util.AuthenticatedUserResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.USERNAME;
import static com.lprakapovich.blog.publicationservice.util.BlogUtil.getBlogId;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Test
    void whenSubscriptionUsernameEqualsSubscriberUsername_exceptionIsThrown() {

        // given
        try (MockedStatic<AuthenticatedUserResolver> resolver = Mockito.mockStatic(AuthenticatedUserResolver.class)) {
            resolver.when(AuthenticatedUserResolver::resolveUsernameFromPrincipal).thenReturn(USERNAME);

            BlogId subscriber = getBlogId("blog1", USERNAME);
            BlogId subscription = getBlogId("blog2", USERNAME);
            Subscription invalidSubscription = new Subscription(subscriber, subscription);

            // when
            // then
            assertThrows(InvalidSubscriptionException.class, () -> subscriptionService.createSubscription(invalidSubscription));
            verify(subscriptionRepository, never()).save(any());
        }
    }

    @Test
    void whenNonExistingSubscriptionIsDeleted_exceptionIsThrown() {

        // given
        BlogId subscriber = getBlogId("blog1", "user1");
        BlogId subscription = getBlogId("blog2", "user2");
        SubscriptionId subscriptionId = new SubscriptionId(subscriber, subscription);
        given(subscriptionRepository.existsById(subscriptionId)).willReturn(false);

        // when
        // then
        assertThrows(SubscriptionNotFoundException.class, () -> subscriptionService.deleteSubscription(subscriptionId));
    }
}
