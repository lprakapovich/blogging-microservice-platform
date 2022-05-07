package com.lprakapovich.blog.publicationservice.model;

import com.lprakapovich.blog.publicationservice.model.Blog.BlogId;
import lombok.*;

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

    @Override
    public boolean equals(Object o) {

        if (o == this)
            return true;

        if (!(o instanceof Subscription))
            return false;

        Subscription other = (Subscription) o;
        return other.getId().equals(id);
    }

    @Embeddable
    @Getter
    @Data
    @EqualsAndHashCode
    @ToString
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
