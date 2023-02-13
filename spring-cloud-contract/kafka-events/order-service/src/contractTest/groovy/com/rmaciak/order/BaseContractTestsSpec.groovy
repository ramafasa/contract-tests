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
import spock.lang.Specification

import javax.annotation.Nullable
import java.util.concurrent.TimeUnit

import static java.util.Collections.emptyMap

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = [OrderApplication.class])
@AutoConfigureMessageVerifier
@ActiveProfiles("test")
abstract class BaseContractTestsSpec extends Specification {

    @Autowired
    private OrderCreator orderCreator

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

    @KafkaListener(topics = ["OrderCreated"])
    void listenOrderCreated(ConsumerRecord payload) {
        consumedEvents.add(MessageBuilder.createMessage(payload.value(), new MessageHeaders(emptyMap())))
    }

    @Override
    Message receive(String destination, long timeout, TimeUnit timeUnit, @Nullable YamlContract contract) {
        for (int i = 0; i < timeout; i++) {
            Message msg = consumedEvents.stream().findFirst().orElse(null)
            if (msg != null) {
                return msg
            }

            Thread.sleep(1000)
        }

        return consumedEvents.stream().findFirst().orElse(null)
    }

    @Override
    Message receive(String destination, YamlContract contract) {
        return receive(destination, 5, TimeUnit.SECONDS, contract)
    }
}