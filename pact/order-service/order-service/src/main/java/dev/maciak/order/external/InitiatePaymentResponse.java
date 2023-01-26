package dev.maciak.order.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InitiatePaymentResponse {
    private String paymentExternalId;
    private String status;
}
