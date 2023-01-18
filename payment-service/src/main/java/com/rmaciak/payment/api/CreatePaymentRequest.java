package com.rmaciak.payment.api;


import com.rmaciak.payment.domain.PaymentType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


public record CreatePaymentRequest(
        UUID accountId,
        BigDecimal quota,
        PaymentType paymentType,
        LocalDateTime dueDate) {

}


