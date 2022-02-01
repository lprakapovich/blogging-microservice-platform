package com.lprakapovich.blog.publicationservice.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class Subscription {

    @EmbeddedId
    SubscriptionId id;

    public Subscription(String blogId, String subscriberBlogId) {
        this.id = new SubscriptionId(blogId, subscriberBlogId);
    }

    @Embeddable
    @Getter
    @EqualsAndHashCode
    public static class SubscriptionId implements Serializable {

        private String blogId;
        private String subscriberBlogId;

        protected SubscriptionId() { }

        public SubscriptionId(String blogId, String subscriberBlogId) {
            this.blogId = blogId;
            this.subscriberBlogId = subscriberBlogId;
        }
    }
}
