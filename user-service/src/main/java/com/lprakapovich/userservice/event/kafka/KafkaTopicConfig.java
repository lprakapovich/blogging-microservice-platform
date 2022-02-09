package com.lprakapovich.userservice.event.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String USER_CREATED_TOPIC = "lprakapovich.user-service.user-created";

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(USER_CREATED_TOPIC).build();
    }
}
