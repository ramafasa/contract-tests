package dev.maciak.order.domain

import au.com.dius.pact.consumer.PactVerificationResult
import au.com.dius.pact.consumer.groovy.PactBuilder
import dev.maciak.order.OrderApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Subject

import static dev.maciak.order.domain.PaymentType.TRANSFER_ONLINE
import static java.util.UUID.randomUUID
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@SpringBootTest(classes = [OrderApplication], webEnvironment = RANDOM_PORT)
class OrderCreatorPactSpec extends Specification {

    @Subject
    @Autowired
    private OrderCreator sut

    private static final def UUID_REGEXP = '[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$'

    def "should create order and execute online payment"() {
        def accountIdVal = randomUUID()
        def orderIdVal = randomUUID()
        def quotaVal = BigDecimal.valueOf(1040.42)

        given: 'payment-service executes online payment correctly'
        def paymentService = new PactBuilder()
        paymentService {
            serviceConsumer "order-service"
            hasPactWith "payment-service"
            port 8088

            uponReceiving('a request to execute online payment')
            withAttributes(
                    method: 'PUT',
                    path: regexp("/payment/$UUID_REGEXP", "/payment/$orderIdVal"),
                    headers: [
                            "Accept"      : APPLICATION_JSON_VALUE,
                            "Content-Type": APPLICATION_JSON_VALUE
                    ]
            )
            withBody {
                accountId uuid()
                paymentType string("TRANSFER_ONLINE")
                quota decimal()
                dueDate datetime()
            }
            willRespondWith(
                    status: 200,
                    headers: ['Content-Type': APPLICATION_JSON_VALUE],
                    body: {
                        paymentExternalId("ext-$orderIdVal")
                        status('FINISHED')
                    }
            )
        }

        when:
        PactVerificationResult result = paymentService.runTest {
            assert sut.createOrder(accountIdVal, orderIdVal, quotaVal, TRANSFER_ONLINE).paymentProcessed()
        }

        then:
        result == new PactVerificationResult.Ok()
    }

    def "should not process order payment when payment was not executed"() {
        def accountIdVal = randomUUID()
        def orderIdVal = randomUUID()
        def quotaVal = BigDecimal.valueOf(1040.42)

        given: 'payment-service returns error due to negative quota'
        def paymentService = new PactBuilder()
        paymentService {

            serviceConsumer "order-service"
            hasPactWith "payment-service"
            port 8088

            uponReceiving('a request to execute online payment with negative quota')
            withAttributes(
                    method: 'PUT',
                    path: regexp("/payment/$UUID_REGEXP", "/payment/$orderIdVal"),
                    headers: [
                            "Accept"      : APPLICATION_JSON_VALUE,
                            "Content-Type": APPLICATION_JSON_VALUE
                    ]
            )
            withBody {
                accountId uuid()
                paymentType string("TRANSFER_ONLINE")
                quota decimal(-780.4)
                dueDate datetime()
            }
            willRespondWith(
                    status: 422,
                    headers: ['Content-Type': APPLICATION_JSON_VALUE],
                    body: {
                        status('FAILED')
                    }
            )
        }

        when:
        PactVerificationResult result = paymentService.runTest {
            assert !sut.createOrder(accountIdVal, orderIdVal, quotaVal, TRANSFER_ONLINE).paymentProcessed()
        }

        then:
        result == new PactVerificationResult.Ok()
    }
}
