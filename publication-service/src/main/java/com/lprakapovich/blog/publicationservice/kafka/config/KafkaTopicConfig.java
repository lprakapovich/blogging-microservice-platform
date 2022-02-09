package com.lprakapovich.blog.publicationservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String USER_CREATED_EVENT_TOPIC = "lprakapovich.user-service.user-created";
    public static final String USER_UPDATED_EVENT_TOPIC = "lprakapovich.user-service.user-updated";
    public static final String BLOG_CREATED_EVENT_TOPIC = "lprakapovich.publication-service.blog-created";
    public static final String PUBLICATION_CREATED_EVENT_TOPIC = "lprakapovich.publication-service.publication-created";

    @Bean
    public NewTopic blogCreatedEventTopic() {
        return build(BLOG_CREATED_EVENT_TOPIC);
    }

    @Bean
    public NewTopic userCreatedEventTopic() {
        return build(USER_CREATED_EVENT_TOPIC);
    }

    @Bean
    public NewTopic publicationCreatedEventTopic() {
        return build(PUBLICATION_CREATED_EVENT_TOPIC);
    }

    @Bean
    public NewTopic userUpdatedEventTopic() {
        return build(USER_UPDATED_EVENT_TOPIC);
    }

    private NewTopic build(String topic) {
        return TopicBuilder.name(topic).build();
    }
}

