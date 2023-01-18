package contracts.orderService

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Validates payment initiation API"
    name "should initiate payment"
    request {
        method 'PUT'
        urlPath $(consumer(regex('/payment/' + uuid())))
        body([
                accountId       : $(consumer(anyUuid())),
                quota           : $(consumer(anyDouble())),
                paymentType     : $(consumer(regex('TRANSFER_(OFFLINE|ONLINE)'))),
                dueDate         : $(consumer(anyDateTime()))
        ])
        headers {
            accept 'application/json'
            contentType 'application/json'
        }
    }
    response {
        status OK()
        body([
                paymentExternalId: "ext-${fromRequest().path(1)}",
                status           : "FINISHED",
        ])
        headers {
            contentType('application/json')
        }
    }
}