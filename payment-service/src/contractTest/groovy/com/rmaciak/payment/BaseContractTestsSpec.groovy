package com.rmaciak.payment

import com.rmaciak.payment.api.PaymentHttpApi
import com.rmaciak.payment.domain.PaymentExecutor
import io.restassured.RestAssured
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

//@SpringBootTest(classes = [PaymentApplication], webEnvironment = RANDOM_PORT, properties = "server.port=8088")
//class BaseContractTestsSpec extends Specification {
//
//    @LocalServerPort
//    private int serverPort;
//
//
//    def setup() {
//        RestAssured.baseURI = "http://localhost:$serverPort"
//    }
//}


abstract class BaseContractTestsSpec extends Specification {
    def setup() {
        RestAssuredMockMvc.standaloneSetup(new PaymentHttpApi(new PaymentExecutor()))
    }
}