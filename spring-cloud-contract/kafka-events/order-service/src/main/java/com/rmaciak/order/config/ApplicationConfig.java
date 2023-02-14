package com.rmaciak.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;

@Configuration
public class ApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic("OrderCreated", 1, (short) 1);
    }
}
