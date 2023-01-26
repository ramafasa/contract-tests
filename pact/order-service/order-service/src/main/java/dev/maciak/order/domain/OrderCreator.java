package dev.maciak.order.domain;


import dev.maciak.order.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderCreator {

    private final PaymentService paymentService;

    public OrderCreationResult createOrder(UUID accountId, UUID orderId, BigDecimal amount, PaymentType paymentType) {
        // some logic here...

        return new OrderCreationResult(
                switch (paymentType) {
                    case TRANSFER_ONLINE -> paymentService.executeOnlinePayment(accountId, orderId, amount);
                    case TRANSFER_OFFLINE -> paymentService.initiateOfflinePayment(accountId, orderId, amount);
                }
        );
    }

}
