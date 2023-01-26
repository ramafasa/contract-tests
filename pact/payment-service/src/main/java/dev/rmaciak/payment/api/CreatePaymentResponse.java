package dev.rmaciak.payment.api;


import dev.rmaciak.payment.domain.PaymentStatus;

public record CreatePaymentResponse(String paymentExternalId, PaymentStatus status) {
}
