package com.rmaciak.payment;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;


@SpringBootApplication
class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }

    @Bean
    public NewTopic orderCreatedTopic() {
        return new NewTopic("OrderCreated", 1, (short) 1);
    }

    @Bean
    public RecordMessageConverter converter() {
        return new StringJsonMessageConverter();
    }
}