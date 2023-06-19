package dev.maciak.order.external;


import dev.maciak.order.domain.PaymentType;
import dev.maciak.order.domain.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static dev.maciak.order.domain.PaymentType.TRANSFER_OFFLINE;
import static dev.maciak.order.domain.PaymentType.TRANSFER_ONLINE;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

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

    @Override
    public boolean initiateOfflinePayment(UUID accountId, UUID orderId, BigDecimal amount) {
        InitiatePaymentResponse response = initiatePayment(accountId, orderId, amount, TRANSFER_OFFLINE);
        return response.getStatus().equals("IN_PROGRESS");
    }

    private InitiatePaymentResponse initiatePayment(
            UUID accountId,
            UUID orderId,
            BigDecimal amount,
            PaymentType paymentType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON);

        HttpEntity<InitiatePaymentRequest> request = new HttpEntity<>(
                new InitiatePaymentRequest(
                        accountId,
                        amount,
                        paymentType,
                        LocalDateTime.ofInstant(clock.instant().plus(1, HOURS), ZoneId.systemDefault()).truncatedTo(SECONDS)
                ),
                headers
        );

        try {
            return restTemplate
                    .exchange("http://localhost:8088/payment/%s".formatted(orderId), PUT, request, InitiatePaymentResponse.class)
                    .getBody();
        } catch (HttpStatusCodeException exception) {
            return InitiatePaymentResponse.withError();
        }
    }

    private record InitiatePaymentRequest(
            UUID accountId,
            BigDecimal quota,
            PaymentType paymentType,
            LocalDateTime dueDate) {
    }
}


