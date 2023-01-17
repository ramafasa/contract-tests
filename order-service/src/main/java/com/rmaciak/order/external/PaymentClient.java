package com.rmaciak.order.external;


import com.rmaciak.order.domain.service.PaymentService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.http.HttpMethod.PUT;

@RequiredArgsConstructor
@Service
public class PaymentClient implements PaymentService {

    private final RestTemplate restTemplate;

    @Override
    public boolean initiatePayment(UUID accountId, UUID orderId, BigDecimal amount) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<InitiatePaymentRequest> request = new HttpEntity<>(
                new InitiatePaymentRequest(accountId, amount),
                headers
        );

        InitiatePaymentResponse response = restTemplate
                .exchange("http://localhost:8088/payment/%s".formatted(orderId), PUT, request, InitiatePaymentResponse.class)
                .getBody();

        return response.getStatus().equals("FINISHED");
    }

    @RequiredArgsConstructor
    @Getter
    @Setter
    public class InitiatePaymentRequest {
        private final UUID accountId;
        private final BigDecimal amount;
    }



}


