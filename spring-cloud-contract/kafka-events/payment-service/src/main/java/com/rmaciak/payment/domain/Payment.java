package com.rmaciak.payment.domain;

import java.math.BigDecimal;
import java.util.UUID;

import static com.rmaciak.payment.domain.PaymentStatus.FINISHED;
import static com.rmaciak.payment.domain.PaymentType.TRANSFER_ONLINE;

public record Payment(
        UUID id,
        UUID externalId,
        BigDecimal quota,
        PaymentType paymentType,
        PaymentStatus paymentStatus
) {

    public static Payment finishedOnlinePayment(UUID paymentId, BigDecimal quota) {
        return new Payment(
                paymentId,
                UUID.randomUUID(),
                quota,
                TRANSFER_ONLINE,
                FINISHED
        );
    }
}
