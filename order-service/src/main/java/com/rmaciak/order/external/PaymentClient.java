package com.rmaciak.order.external;


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

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.springframework.http.HttpMethod.PUT;

@RequiredArgsConstructor
@Service
public class PaymentClient implements PaymentService {

    private final RestTemplate restTemplate;
    private final Clock clock;

    @Override
    public boolean initiatePayment(UUID accountId, UUID orderId, BigDecimal amount) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<InitiatePaymentRequest> request = new HttpEntity<>(
                new InitiatePaymentRequest(
                        accountId,
                        amount,
                        "TRANSFER_OFFLINE",
                        LocalDateTime.ofInstant(clock.instant().plus(1, HOURS), ZoneId.systemDefault()).truncatedTo(SECONDS)
                ),
                headers
        );

        InitiatePaymentResponse response = restTemplate
                .exchange("http://localhost:8088/payment/%s".formatted(orderId), PUT, request, InitiatePaymentResponse.class)
                .getBody();

        return response.getStatus().equals("FINISHED");
    }

    public record InitiatePaymentRequest(
            UUID accountId,
            BigDecimal quota,
            String paymentType,
            LocalDateTime dueDate) {
    }
}


