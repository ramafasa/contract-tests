package contracts.orderService

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should produce OrderCreated event when order was created")
    label("triggerOrderCreatedEvent")
    input {
        triggeredBy("createOrder()")
    }
    outputMessage {
        sentTo("OrderCreated")
        body([
                orderId: anyUuid(),
                accountId: anyUuid(),
                total: 120.80,
                isPaid: true
        ])
    }
}