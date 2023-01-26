package dev.maciak.order.domain.service;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {
    boolean executeOnlinePayment(UUID accountId, UUID orderId, BigDecimal amount);
    boolean initiateOfflinePayment(UUID accountId, UUID orderId, BigDecimal amount);
}
