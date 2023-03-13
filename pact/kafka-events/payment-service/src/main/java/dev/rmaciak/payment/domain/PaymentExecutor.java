package dev.rmaciak.payment.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PaymentExecutor {

    private final PaymentRepository paymentRepository;

    public Payment initiatePayment(
            UUID paymentId,
            BigDecimal quota
    ) {

        if (quota.signum() != 1) {
            throw new NonPositivePaymentQuotaException();
        }

        var payment = Payment.finishedOnlinePayment(
                paymentId,
                quota
        );

        paymentRepository.save(payment);
        return payment;
    }
}
