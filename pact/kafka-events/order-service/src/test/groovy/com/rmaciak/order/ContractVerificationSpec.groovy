package com.rmaciak.order

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit5.MessageTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import com.rmaciak.order.domain.OrderCreator
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.Message
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@Provider("order-service")
@PactBroker(url = "http://localhost:9292")
@SpringBootTest(classes = OrderApplication, webEnvironment = NONE)
@Testcontainers
class ContractVerificationSpec extends Specification {

    @Shared
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

    @Shared
    private static final Set<String> consumedEvents = Collections.synchronizedSet(new HashSet<String>())

    @Autowired
    OrderCreator orderCreator

    @DynamicPropertySource
    static void registerKafkaBootstrapServerProperty(DynamicPropertyRegistry registry) {
        kafka.start()
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers())
    }

    @KafkaListener(topics = "OrderCreated", groupId = "order-test-consumer")
    void orderCreatedEventListener(ConsumerRecord record) {
        consumedEvents.add((String) record.value())
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider)
    void testTemplate(PactVerificationContext context) {
        context.verifyInteraction()
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget())
    }

    @PactVerifyProvider('OrderCreatedEvent when order is created')
    String "should publish OrderCreated event"() {
        orderCreator.createOrder(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(10.5))

        new PollingConditions(timeout: 10).eventually {
            assert consumedEvents.stream().findFirst().isPresent()
        }

        return consumedEvents.stream().findFirst().orElse(null)

    }
}
