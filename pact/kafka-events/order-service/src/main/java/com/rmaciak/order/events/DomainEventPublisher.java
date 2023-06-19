package com.rmaciak.order.events;

public interface DomainEventPublisher {
    void publish(DomainEvent domainEvent);
}
