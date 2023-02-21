package com.rmaciak.order.domain;


import com.rmaciak.order.domain.service.PaymentService;
import com.rmaciak.order.events.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;

@RequiredArgsConstructor
@Service
public class OrderCreator {

    private final PaymentService paymentService;
    private final DomainEventPublisher eventPublisher;
    private final Clock clock;

    public OrderCreationResult createOrder(UUID accountId, UUID orderId, BigDecimal quota) {
        // some logic here...

        boolean isOrderPaid = paymentService.executeOnlinePayment(accountId, orderId, quota);

        eventPublisher.publish(
                new OrderCreatedEvent(
                        UUID.randomUUID(),
                        LocalDateTime.now(clock).truncatedTo(SECONDS),
                        orderId,
                        accountId,
                        quota,
                        isOrderPaid
                )
        );

        return new OrderCreationResult(isOrderPaid);
    }
}
