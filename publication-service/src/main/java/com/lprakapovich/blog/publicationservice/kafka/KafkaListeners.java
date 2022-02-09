package com.lprakapovich.blog.publicationservice.kafka;

import com.lprakapovich.blog.publicationservice.kafka.config.KafkaTopicConfig;
import com.lprakapovich.blog.publicationservice.kafka.events.BlogCreatedEvent;
import com.lprakapovich.blog.publicationservice.kafka.events.PublicationCreatedEvent;
import com.lprakapovich.blog.publicationservice.kafka.events.UserRegisteredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(topics = KafkaTopicConfig.USER_CREATED_EVENT_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "concurrentKafkaListenerContainerFactory")
    void handleUserRegisteredEvent(UserRegisteredEvent event) {
    }

    @KafkaListener(topics = KafkaTopicConfig.PUBLICATION_CREATED_EVENT_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "concurrentKafkaListenerContainerFactory")
    void handlePublicationCreatedEvent(PublicationCreatedEvent event) {
    }

    @KafkaListener(topics = KafkaTopicConfig.BLOG_CREATED_EVENT_TOPIC, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "concurrentKafkaListenerContainerFactory")
    void handleBlogCreatedEvent(BlogCreatedEvent event) {
    }
}
