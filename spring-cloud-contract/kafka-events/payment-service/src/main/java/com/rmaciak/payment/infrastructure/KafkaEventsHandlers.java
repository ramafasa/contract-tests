package com.rmaciak.payment.infrastructure;

import com.rmaciak.payment.domain.PaymentExecutor;
import com.rmaciak.payment.events.DomainEventsHandler;
import com.rmaciak.payment.events.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@RequiredArgsConstructor
@Configuration
public class KafkaEventsHandlers implements DomainEventsHandler {

    private final PaymentExecutor paymentExecutor;

    @Override
    @KafkaListener(topics = "OrderCreated", groupId = "payment-service-consumer")
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        paymentExecutor.initiatePayment(event.getOrderId(), event.getTotal());
    }
}
