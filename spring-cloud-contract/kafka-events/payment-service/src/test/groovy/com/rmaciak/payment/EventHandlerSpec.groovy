package com.rmaciak.payment

import com.rmaciak.payment.domain.PaymentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.stubrunner.StubTrigger
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner
import org.springframework.cloud.contract.verifier.converter.YamlContract
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHeaders
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.spock.Testcontainers
import org.testcontainers.utility.DockerImageName
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import javax.annotation.Nullable

import static com.rmaciak.payment.domain.PaymentStatus.FINISHED
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL
import static org.springframework.kafka.support.KafkaHeaders.TOPIC
import static org.springframework.messaging.support.MessageBuilder.createMessage

@SpringBootTest(webEnvironment = NONE, classes = [PaymentApplication.class, TestConfiguration.class])
@AutoConfigureStubRunner(ids = ["com.rmaciak:order-service:0.0.1-SNAPSHOT:stubs:8088"], stubsMode = LOCAL)
@Testcontainers
class EventHandlerSpec extends Specification {

    @Shared
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

    @Autowired
    PaymentRepository paymentRepository

    @Autowired
    StubTrigger stubTrigger

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        kafka.start()
        registry.add("spring.kafka.bootstrap-servers", () -> kafka.getBootstrapServers())
    }

    def "should initiate payment on OrderCreatedEvent"() {
        given:
        def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)

        when:
        stubTrigger.trigger("triggerOrderCreatedEvent")

        then:
        conditions.eventually {
            def payments = paymentRepository.getAll()
            assert payments.size() == 1
            assert payments[0].paymentStatus() == FINISHED
        }
    }
}

@Configuration
class TestConfiguration {

    @Bean
    MessageVerifierSender<Message<?>> standaloneMessageVerifier(KafkaTemplate kafkaTemplate) {
        return new MessageVerifierSender<Message<?>>() {

            @Override
            void send(Message<?> message, String destination, @Nullable YamlContract contract) {
                kafkaTemplate.send(message)
            }

            @Override
            <T> void send(T payload, Map<String, Object> headers, String destination, @Nullable YamlContract contract) {
                kafkaTemplate.send(
                        createMessage(payload, new MessageHeaders(Map.of(TOPIC, destination)))
                )
            }
        }
    }
}