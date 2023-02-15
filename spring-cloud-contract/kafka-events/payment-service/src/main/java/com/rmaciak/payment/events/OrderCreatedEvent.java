package com.rmaciak.payment.events;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreatedEvent extends DomainEvent {

    private UUID eventId;
    private Instant occurredAt;

    private UUID orderId;
    private UUID accountId;
    private BigDecimal total;
    private Boolean isPaid;

    @Override
    public String getTopic() {
        return "OrderCreated";
    }
}
