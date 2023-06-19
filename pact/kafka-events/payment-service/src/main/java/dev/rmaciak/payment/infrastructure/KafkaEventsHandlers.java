package dev.rmaciak.payment.infrastructure;

import dev.rmaciak.payment.domain.PaymentExecutor;
import dev.rmaciak.payment.events.DomainEventsHandler;
import dev.rmaciak.payment.events.OrderCreatedEvent;
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
