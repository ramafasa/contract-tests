package com.rmaciak.order.domain;


import com.rmaciak.order.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OrderCreator {

    private final PaymentService paymentService;

    public OrderCreationResult createOrder(UUID accountId, UUID orderId, BigDecimal quota) {
        // some logic here...
        return new OrderCreationResult(paymentService.executeOnlinePayment(accountId, orderId, quota));
    }
}
