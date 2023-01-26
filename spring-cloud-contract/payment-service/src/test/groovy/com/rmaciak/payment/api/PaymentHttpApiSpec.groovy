package com.rmaciak.payment.api

import com.rmaciak.payment.PaymentApplication
import io.restassured.RestAssured
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import spock.lang.Specification

import java.time.LocalDateTime

import static io.restassured.RestAssured.given
import static java.util.UUID.randomUUID
import static org.hamcrest.Matchers.equalTo
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@SpringBootTest(classes = [PaymentApplication], webEnvironment = RANDOM_PORT)
class PaymentHttpApiSpec extends Specification {

    @LocalServerPort
    private int serverPort;

    def setup() {
        RestAssured.baseURI = "http://localhost"
        RestAssured.port = serverPort
    }

    def "should initiate payment"() {
        given:
        def accountId = randomUUID()
        def paymentId = randomUUID()
        def dueDate = LocalDateTime.now().plusHours(1).toString()

        def request =
                """
                {
                    "accountId": "$accountId",
                    "orderDescription": "My first order",
                    "quota": 128.5,
                    "paymentType": "TRANSFER_ONLINE",
                    "dueDate": "$dueDate"  
                }        
                """

        expect:
        given()
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .and()
                .body(request)
                .when()
                .put("/payment/%s".formatted(paymentId))
                .then()
                .assertThat()
                .statusCode(200)
                .body("status", equalTo("FINISHED"))
                .body("paymentExternalId", equalTo("ext-%s".formatted(paymentId)))
    }
}
