package com.rmaciak.order.domain.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    boolean initiatePayment(UUID accountId, UUID orderId, BigDecimal amount);
}
