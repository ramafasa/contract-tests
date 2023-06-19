package com.rmaciak.order.external;

import com.rmaciak.order.events.DomainEvent;
import com.rmaciak.order.events.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaEventPublisher implements DomainEventPublisher {

    private final KafkaTemplate<String, DomainEvent> template;

    @Override
    public void publish(DomainEvent domainEvent) {
        template.send(domainEvent.getTopic(), domainEvent);
    }
}
