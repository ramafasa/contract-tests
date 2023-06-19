package dev.rmaciak.payment.domain;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static dev.rmaciak.payment.domain.PaymentStatus.FINISHED;
import static dev.rmaciak.payment.domain.PaymentStatus.IN_PROGRESS;
import static dev.rmaciak.payment.domain.PaymentType.TRANSFER_ONLINE;

@Service
public class PaymentExecutor {

    public PaymentStatus initiatePayment(
            BigDecimal quota,
            PaymentType type
    ) {

        if (quota.signum() != 1) {
            throw new NonPositivePaymentQuotaException();
        }

        if (type == TRANSFER_ONLINE) {
            return FINISHED;
        } else {
            return IN_PROGRESS;
        }
    }
}
