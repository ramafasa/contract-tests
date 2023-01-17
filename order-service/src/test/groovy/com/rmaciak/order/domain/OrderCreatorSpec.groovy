import com.rmaciak.order.domain.OrderCreator
import com.rmaciak.order.utils.WireMockSpec
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static java.util.UUID.randomUUID

class OrderCreatorSpec extends WireMockSpec {

    @Subject
    @Autowired
    private OrderCreator sut

    def "should create order and initiate payment"() {
        given:
        def accountId = randomUUID()
        def orderId = randomUUID()
        def quote = BigDecimal.valueOf(1050L)

        def paymentServiceResponse =
                """
                {
                    "paymentExternalId": "ext-$orderId",
                    "status": "FINISHED"
                }        
                """

        and:
        wiremockServer.stubFor(put(urlMatching("/payment/([a-fA-F0-9-]*)"))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withHeader("Accept", "application/json")
                                .withBody(paymentServiceResponse)
                )
        )

        expect:
        sut.createOrder(accountId, orderId, quote).paymentProcessed() == true
    }
}