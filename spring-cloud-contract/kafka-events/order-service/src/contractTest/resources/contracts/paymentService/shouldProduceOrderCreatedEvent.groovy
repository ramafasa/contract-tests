package contracts.paymentService

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
                eventId: anyUuid(),
                occurredAt: anyDateTime(),
                orderId: anyUuid(),
                accountId: anyUuid(),
                total: $(consumer(regex('^(\\d+\\.\\d+)$')))
        ])
    }
}