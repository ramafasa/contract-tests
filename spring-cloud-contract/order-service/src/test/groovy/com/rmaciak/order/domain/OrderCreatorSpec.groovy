package com.rmaciak.order.domain

import com.rmaciak.order.utils.WireMockSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneId

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.rmaciak.order.domain.PaymentType.TRANSFER_ONLINE
import static java.time.temporal.ChronoUnit.HOURS
import static java.time.temporal.ChronoUnit.SECONDS
import static java.util.UUID.randomUUID

class OrderCreatorSpec extends WireMockSpec {

    @Subject
    @Autowired
    private OrderCreator sut

    @Autowired
    private Clock clock

    def "should create order and execute online payment"() {
        given:
        def accountId = randomUUID()
        def orderId = randomUUID()
        def quota = BigDecimal.valueOf(1050L)
        def dueDate = LocalDateTime.ofInstant(clock.instant().plus(1, HOURS), ZoneId.systemDefault()).truncatedTo(SECONDS)

        def expectedRequest =
                """
                {
                    "accountId": "$accountId",
                    "quota": $quota,
                    "paymentType": "TRANSFER_ONLINE",
                    "dueDate": "$dueDate"                        
                }        
                """

        def paymentServiceResponse =
                """
                {
                    "paymentExternalId": "ext-$orderId",
                    "status": "FINISHED"
                }        
                """

        and:
        wiremockServer.stubFor(put(urlMatching("/payment/([a-fA-F0-9-]*)"))
                .withRequestBody(equalToJson(expectedRequest))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Accept", "application/json")
                                .withBody(paymentServiceResponse)
                )
        )

        expect:
        sut.createOrder(accountId, orderId, quota, TRANSFER_ONLINE).paymentProcessed()
    }
}