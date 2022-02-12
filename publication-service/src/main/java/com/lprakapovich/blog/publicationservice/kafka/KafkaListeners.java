package com.lprakapovich.blog.publicationservice.kafka;

import com.lprakapovich.blog.publicationservice.kafka.config.KafkaTopicConfig;
import com.lprakapovich.blog.publicationservice.kafka.events.BlogCreatedEvent;
import com.lprakapovich.blog.publicationservice.kafka.events.PublicationCreatedEvent;
import com.lprakapovich.blog.publicationservice.kafka.events.UserRegisteredEvent;
import com.lprakapovich.blog.publicationservice.model.Blog;
import com.lprakapovich.blog.publicationservice.service.BlogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class KafkaListeners {

    private final BlogService blogService;

    @KafkaListener(topics = KafkaTopicConfig.USER_CREATED_EVENT_TOPIC, containerFactory = "concurrentKafkaListenerContainerFactory")
    void handleUserRegisteredEvent(UserRegisteredEvent event) {
        Blog blog = Blog.builder()
                .id(event.getUsername())
                .build();
        blogService.createBlog(blog);
    }

    @KafkaListener(topics = KafkaTopicConfig.PUBLICATION_CREATED_EVENT_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "concurrentKafkaListenerContainerFactory")
    void handlePublicationCreatedEvent(PublicationCreatedEvent event) {
    }

    @KafkaListener(topics = KafkaTopicConfig.BLOG_CREATED_EVENT_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "concurrentKafkaListenerContainerFactory")
    void handleBlogCreatedEvent(BlogCreatedEvent event) {
    }
}
