package com.rmaciak.payment.infrastructure;

import com.rmaciak.payment.domain.Payment;
import com.rmaciak.payment.domain.PaymentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<UUID, Payment> payments = new HashMap<>();

    @Override
    public Payment save(Payment payment) {
        payments.put(payment.id(), payment);
        return payment;
    }

    @Override
    public Set<Payment> getAll() {
        return new HashSet<>(payments.values());
    }
}
