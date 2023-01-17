package com.rmaciak.payment.domain;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentExecutor {

    public PaymentStatus initiatePayment(
            UUID accountId,
            BigDecimal quota,
            PaymentType type,
            LocalDateTime dueDate

    ) {

        if (quota.signum() != 1) {
            throw new NonPositivePaymentQuotaException();
        }

        return PaymentStatus.FINISHED;
    }
}
