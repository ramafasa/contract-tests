package dev.maciak.order;

import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.MockServerConfig;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import dev.maciak.order.domain.OrderCreator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spock.lang.Subject;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody;
import static au.com.dius.pact.consumer.dsl.PactDslRequestBase.CONTENT_TYPE;
import static dev.maciak.order.domain.PaymentType.TRANSFER_ONLINE;
import static java.util.UUID.randomUUID;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ExtendWith(PactConsumerTestExt.class)
@MockServerConfig(port = "8088")
@SpringBootTest(classes = {OrderApplication.class}, webEnvironment = RANDOM_PORT)
public class OrderCreatorPactTest {

    @Subject
    @Autowired
    private OrderCreator sut;

    @Pact(provider = "payment-service", consumer = "order-service")
    public RequestResponsePact onlinePaymentExecutionPact(PactDslWithProvider builder) {
        return builder
                .uponReceiving("a request to execute online payment")
                .path("/payment/68cbc20f-3318-4270-81be-7937250f108f")
                .method("PUT")
                .headers(
                        ACCEPT, APPLICATION_JSON_VALUE,
                        CONTENT_TYPE, APPLICATION_JSON_VALUE
                )
                .body(
                        new PactDslJsonBody()
                                .uuid("accountId")
                                .decimalType("quota")
                                .stringValue("paymentType", "TRANSFER_ONLINE")
                                .datetime("dueDate")
                )
                .willRespondWith()
                .status(200)
                .headers(Map.of(
                                CONTENT_TYPE, APPLICATION_JSON_VALUE
                        )
                )
                .body(
                        newJsonBody(json -> {
                                    json.stringValue("paymentExternalId", "ex-68cbc20f-3318-4270-81be-7937250f108f");
                                    json.stringValue("status", "FINISHED");
                                }
                        ).build()
                ).toPact();
    }

    @Pact(provider = "payment-service", consumer = "order-service")
    public RequestResponsePact onlinePaymentExecutionError(PactDslWithProvider builder) {
        return builder
                .uponReceiving("a request to execute online payment with negative quota")
                .path("/payment/68cbc20f-3318-4270-81be-7937250f108f")
                .method("PUT")
                .headers(
                        ACCEPT, APPLICATION_JSON_VALUE,
                        CONTENT_TYPE, APPLICATION_JSON_VALUE
                )
                .body(
                        new PactDslJsonBody()
                                .uuid("accountId")
                                .decimalMatching("quota", "^-[0-9]+[.]{0,1}[0-9]+$", -789.5)
                                .stringValue("paymentType", "TRANSFER_ONLINE")
                                .datetime("dueDate")
                )
                .willRespondWith()
                .status(422)
                .headers(Map.of(
                                CONTENT_TYPE, APPLICATION_JSON_VALUE
                        )
                )
                .body(
                        newJsonBody(json -> json.stringValue("status", "FAILED")
                        )
                                .build()
                ).toPact();
    }

    @Test
    @PactTestFor(pactMethod = "onlinePaymentExecutionPact")
    void shouldCreateOrder_andProcessOrderPayment_whenPaymentWasExecuted() {
        // given
        UUID accountId = randomUUID();
        UUID orderId = UUID.fromString("68cbc20f-3318-4270-81be-7937250f108f");
        BigDecimal quota = BigDecimal.valueOf(1050.5);

        // when
        var actual = sut.createOrder(accountId, orderId, quota, TRANSFER_ONLINE);

        // then
        Assertions.assertTrue(actual.paymentProcessed());
    }

    @Test
    @PactTestFor(pactMethod = "onlinePaymentExecutionError")
    void shouldNotProcessOrderPayment_whenPaymentWasNotExecuted() {
        // given
        UUID accountId = randomUUID();
        UUID orderId = UUID.fromString("68cbc20f-3318-4270-81be-7937250f108f");
        BigDecimal quota = BigDecimal.valueOf(-1050.5);

        // when
        var actual = sut.createOrder(accountId, orderId, quota, TRANSFER_ONLINE);

        // then
        Assertions.assertFalse(actual.paymentProcessed());
    }
}
