package com.rmaciak.payment.api;

import com.rmaciak.payment.domain.PaymentStatus;


public record CreatePaymentResponse(String paymentExternalId, PaymentStatus status) {
}
