package com.rmaciak.order.domain;

import com.rmaciak.order.events.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class OrderCreatedEvent extends DomainEvent {

    private final UUID eventId;
    private final LocalDateTime occurredAt;
    private final UUID orderId;
    private final UUID accountId;
    private final BigDecimal total;
    private final Boolean isPaid;

    @Override
    public String getTopic() {
        return "OrderCreated";
    }
}
