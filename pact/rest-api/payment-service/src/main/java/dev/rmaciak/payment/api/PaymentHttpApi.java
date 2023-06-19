package dev.rmaciak.payment.api;

import dev.rmaciak.payment.domain.NonPositivePaymentQuotaException;
import dev.rmaciak.payment.domain.PaymentExecutor;
import dev.rmaciak.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class PaymentHttpApi {

    private final PaymentExecutor paymentExecutor;

    @ExceptionHandler({NonPositivePaymentQuotaException.class})
    public ResponseEntity<CreatePaymentResponse> handleException() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return new ResponseEntity<>(new CreatePaymentResponse(null, PaymentStatus.FAILED), headers, UNPROCESSABLE_ENTITY);
    }
}
