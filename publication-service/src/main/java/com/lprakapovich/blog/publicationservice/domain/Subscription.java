package com.lprakapovich.blog.publicationservice.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@NoArgsConstructor
public class Subscription {

    @EmbeddedId
    SubscriptionId id;

    public Subscription(long blogId, long subscriberBlogId) {
        this.id = new SubscriptionId(blogId, subscriberBlogId);
    }

    @Embeddable
    @Getter
    public static class SubscriptionId implements Serializable {

        private long blogId;
        private long subscriberBlogId;

        protected SubscriptionId() { }

        public SubscriptionId(long blogId, long subscriberBlogId) {
            this.blogId = blogId;
            this.subscriberBlogId = subscriberBlogId;
        }
    }
}
