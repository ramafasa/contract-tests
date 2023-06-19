package dev.rmaciak.payment.domain;

import java.util.Set;

public interface PaymentRepository {

    Payment save(Payment payment);
    Set<Payment> getAll();
}
