package com.rmaciak.order

import com.rmaciak.order.domain.OrderCreator
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.verifier.converter.YamlContract
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierReceiver
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification

import javax.annotation.Nullable
import java.util.concurrent.TimeUnit

import static java.util.Collections.emptyMap
import static java.util.concurrent.TimeUnit.SECONDS
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE

@SpringBootTest(webEnvironment = NONE, classes = [OrderApplication.class])
@AutoConfigureMessageVerifier
@ActiveProfiles("test")
@Testcontainers
abstract class BaseContractTestsSpec extends Specification {

    @Shared
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

    @Autowired
    private OrderCreator orderCreator

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        kafka.start()
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers())
    }

    void createOrder() {
        orderCreator.createOrder(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(120.8)
        )
    }
}

@EnableKafka
@Configuration
class ContractTestsConfiguration {

    @Bean
    KafkaEventVerifier kafkaEventVerifier() {
        return new KafkaEventVerifier()
    }
}

class KafkaEventVerifier implements MessageVerifierReceiver<Message<?>> {

    private final Set<Message> consumedEvents = Collections.synchronizedSet(new HashSet<Message>())

    @KafkaListener(topics = ["OrderCreated"], groupId = "order-consumer")
    void consumeOrderCreated(ConsumerRecord payload) {
        consumedEvents.add(MessageBuilder.createMessage(payload.value(), new MessageHeaders(emptyMap())))
    }

    @Override
    Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
        for (int i = 0; i < timeout; i++) {
            Message msg = consumedEvents.stream().findFirst().orElse(null)
            if (msg != null) {
                return msg
            }

            timeUnit.sleep(1)
        }

        return consumedEvents.stream().findFirst().orElse(null)
    }

    @Override
    Message receive(String destination, YamlContract contract) {
        return receive(destination, 5, SECONDS, contract)
    }
}