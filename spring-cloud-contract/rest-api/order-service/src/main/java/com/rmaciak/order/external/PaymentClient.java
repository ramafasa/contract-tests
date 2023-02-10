package com.rmaciak.order.external;


import com.rmaciak.order.domain.PaymentType;
import com.rmaciak.order.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static com.rmaciak.order.domain.PaymentType.TRANSFER_ONLINE;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.springframework.http.HttpMethod.PUT;

@RequiredArgsConstructor
@Service
public class PaymentClient implements PaymentService {

    private final RestTemplate restTemplate;
    private final Clock clock;

    @Override
    public boolean executeOnlinePayment(UUID accountId, UUID orderId, BigDecimal amount) {
        InitiatePaymentResponse response = initiatePayment(accountId, orderId, amount, TRANSFER_ONLINE);
        return response.getStatus().equals("FINISHED");
    }

    private InitiatePaymentResponse initiatePayment(
            UUID accountId,
            UUID orderId,
            BigDecimal amount,
            PaymentType paymentType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<InitiatePaymentRequest> request = new HttpEntity<>(
                new InitiatePaymentRequest(
                        accountId,
                        amount,
                        paymentType,
                        LocalDateTime.ofInstant(clock.instant().plus(1, HOURS), ZoneId.systemDefault()).truncatedTo(SECONDS)
                ),
                headers
        );

        return restTemplate
                .exchange("http://localhost:8088/payment/%s".formatted(orderId), PUT, request, InitiatePaymentResponse.class)
                .getBody();
    }

    private record InitiatePaymentRequest(
            UUID accountId,
            BigDecimal quota,
            PaymentType paymentType,
            LocalDateTime dueDate) {
    }
}


