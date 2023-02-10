package com.rmaciak.order.domainimport com.rmaciak.order.OrderApplicationimport org.springframework.beans.factory.annotation.Autowiredimport org.springframework.boot.test.context.SpringBootTestimport org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunnerimport org.springframework.cloud.contract.stubrunner.spring.StubRunnerPropertiesimport spock.lang.Specificationimport spock.lang.Subjectimport static com.rmaciak.order.domain.PaymentType.TRANSFER_OFFLINEimport static com.rmaciak.order.domain.PaymentType.TRANSFER_ONLINEimport static java.util.UUID.randomUUID@SpringBootTest(classes = [OrderApplication])@AutoConfigureStubRunner(        ids = ["com.rmaciak:payment-service:0.0.1-SNAPSHOT:stubs:8088"],        stubsMode = StubRunnerProperties.StubsMode.LOCAL)class OrderCreatorWithContractSpec extends Specification {    @Subject    @Autowired    private OrderCreator sut    def "should create order and execute online payment"() {        given:        def accountId = randomUUID()        def orderId = randomUUID()        def quota = BigDecimal.valueOf(1050.0)        expect:        sut.createOrder(accountId, orderId, quota).paymentProcessed()    }}