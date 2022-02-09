package com.lprakapovich.blog.publicationservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String USER_CREATED_EVENT_TOPIC = "lprakapovich.user-service.user-created";
    public static final String BLOG_CREATED_EVENT_TOPIC = "lprakapovich.publication-service.blog-created";
    public static final String PUBLICATION_CREATED_EVENT_TOPIC = "lprakapovich.publication-service.publication-created";

    @Bean
    public NewTopic blogCreatedEventTopic() {
        return TopicBuilder.name(BLOG_CREATED_EVENT_TOPIC).build();
    }

    @Bean
    public NewTopic userCreatedEventTopic() {
        return TopicBuilder.name(USER_CREATED_EVENT_TOPIC).build();
    }

    @Bean
    public NewTopic publicationCreatedEventTopic() {
        return TopicBuilder.name(PUBLICATION_CREATED_EVENT_TOPIC).build();
    }
}

