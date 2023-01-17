package com.rmaciak.payment.api;

import com.rmaciak.payment.domain.PaymentExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class PaymentHttpApi {

    private final PaymentExecutor paymentExecutor;

    @PutMapping(value = "/payment/{paymentId}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CreatePaymentResponse createPayment(
            @PathVariable UUID paymentId,
            @RequestBody CreatePaymentRequest request) {

        var response = new CreatePaymentResponse(
                "ext-" + paymentId,
                paymentExecutor.initiatePayment(request.accountId(), request.quota(), request.paymentType(), request.dueDate())
        );

        return response;
    }

}
