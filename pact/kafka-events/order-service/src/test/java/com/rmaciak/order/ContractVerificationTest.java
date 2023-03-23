package com.rmaciak.order;

import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import com.rmaciak.order.domain.OrderCreator;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@Provider("order-service")
@PactBroker(url = "http://localhost:9292")
@SpringBootTest(webEnvironment = NONE)
public class ContractVerificationTest {

    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
    private static final Set<String> consumedEvents = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private OrderCreator orderCreator;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        kafka.start();
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers());
    }

    @KafkaListener(topics = "OrderCreated", groupId = "order-test-consumer")
    public void orderCreatedEventListener(ConsumerRecord record) {
        consumedEvents.add((String) record.value());
    }

    @BeforeEach
    void beforeEach(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @PactVerifyProvider("OrderCreatedEvent when order is created")
    MessageAndMetadata shouldPublishOrderCreatedEvent() {
        orderCreator.createOrder(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.valueOf(10.5));

        Awaitility.await()
                .until(() -> consumedEvents.size() != 0);

        var event = consumedEvents.stream().findFirst().orElseThrow();
        return new MessageAndMetadata(event.getBytes(), Map.of());
    }
}
