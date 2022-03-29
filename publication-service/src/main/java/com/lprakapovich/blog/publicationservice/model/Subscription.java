package com.lprakapovich.blog.publicationservice.model;

import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
public class Subscription {

    @EmbeddedId
    private SubscriptionId id;

    public Subscription(BlogId subscriber, BlogId subscription) {
        this.id = new SubscriptionId(subscriber, subscription);
    }

    @Embeddable
    @Getter
    @Data
    public static class SubscriptionId implements Serializable {

        @AttributeOverride(name="id", column= @Column(name="subscriberBlogId"))
        @AttributeOverride(name="username", column= @Column(name="subscriberUsername"))
        private BlogId subscriber;

        @AttributeOverride(name="id", column= @Column(name="subscriptionBlogId"))
        @AttributeOverride(name="username", column= @Column(name="subscriptionUsername"))
        private BlogId subscription;

        protected SubscriptionId() { }

        public SubscriptionId(BlogId subscriber, BlogId subscription) {
            this.subscriber = subscriber;
            this.subscription = subscription;
        }
    }
}
