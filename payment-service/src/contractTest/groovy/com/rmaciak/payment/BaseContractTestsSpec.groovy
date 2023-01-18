package com.rmaciak.payment

import com.rmaciak.payment.api.PaymentHttpApi
import com.rmaciak.payment.domain.PaymentExecutor
import io.restassured.module.mockmvc.RestAssuredMockMvc
import spock.lang.Specification

abstract class BaseContractTestsSpec extends Specification {

    def setup() {
        RestAssuredMockMvc.standaloneSetup(new PaymentHttpApi(new PaymentExecutor()))
    }

}
