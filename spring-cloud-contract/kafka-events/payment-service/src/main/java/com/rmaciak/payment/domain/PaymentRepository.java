package com.rmaciak.payment.domain;

import java.util.Set;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);
    Set<Payment> getAll();
}
