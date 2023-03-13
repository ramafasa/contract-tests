package dev.rmaciak.payment.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderCreatedEvent extends DomainEvent {

    private UUID eventId;
    private LocalDateTime occurredAt;

    private UUID orderId;
    private UUID accountId;
    private BigDecimal total;
    private Boolean isPaid;

    @Override
    public String getTopic() {
        return "OrderCreated";
    }
}
