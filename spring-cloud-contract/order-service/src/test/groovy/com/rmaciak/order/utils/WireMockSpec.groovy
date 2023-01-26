package com.rmaciak.order.utils

import com.github.tomakehurst.wiremock.WireMockServer
import com.rmaciak.order.OrderApplication
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Shared
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(classes = [OrderApplication], webEnvironment = RANDOM_PORT)
class WireMockSpec extends Specification {

    @Shared
    WireMockServer wiremockServer

    def setupSpec() {
        wiremockServer = new WireMockServer(options().port(8088))
        wiremockServer.start()
    }

    def cleanupSpec() {
        wiremockServer.stop()
    }

    def cleanup() {
        wiremockServer.resetAll()
    }
}
