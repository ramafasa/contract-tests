package dev.rmaciak.payment

import au.com.dius.pact.provider.ConsumerInfo
import au.com.dius.pact.provider.ProviderInfo
import au.com.dius.pact.provider.ProviderVerifier
import au.com.dius.pact.provider.VerificationResult
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ContractVerificationSpec extends Specification {

    @LocalServerPort
    private int port

    @Shared
    ProviderInfo serviceProvider
    ProviderVerifier verifier

    def setupSpec() {
        serviceProvider = new ProviderInfo("payment-service")
        serviceProvider.hasPactsFromPactBroker("http://localhost:9292")
    }

    def setup() {
        verifier = new ProviderVerifier()
        serviceProvider.port = port
    }

    def 'verify contract with #consumer'() {
        expect:
        verifyConsumerPact(consumer) instanceof VerificationResult.Ok

        where:
        consumer << serviceProvider.consumers
    }

    private VerificationResult verifyConsumerPact(ConsumerInfo consumer) {
        verifier.initialiseReporters(serviceProvider)
        def testResult = verifier.runVerificationForConsumer([:], serviceProvider, consumer)

        if (testResult instanceof VerificationResult.Failed) {
            verifier.displayFailures([testResult])
        }

        testResult
    }
}
