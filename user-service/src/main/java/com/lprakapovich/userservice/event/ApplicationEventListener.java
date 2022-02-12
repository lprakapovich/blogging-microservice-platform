package com.lprakapovich.userservice.event;


import com.lprakapovich.userservice.event.kafka.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @EventListener
    public void publishUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        kafkaTemplate.send(KafkaTopicConfig.USER_CREATED_TOPIC, userCreatedEvent.getUsername(), userCreatedEvent);
    }
}
