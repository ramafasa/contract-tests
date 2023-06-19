package dev.rmaciak.payment.events;


public interface DomainEventsHandler {

    void handleOrderCreatedEvent(OrderCreatedEvent event);
}
