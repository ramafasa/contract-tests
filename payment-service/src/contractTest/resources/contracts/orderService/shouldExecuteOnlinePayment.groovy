package contracts.orderService

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Validates that payment API accepts online payment"
    name "should execute online payment"
    request {
        method 'PUT'
        urlPath $(consumer(regex('/payment/' + uuid())))
        body([
                accountId  : $(consumer(anyUuid())),
                quota      : $(consumer(regex('^(\\d+\\.\\d+)$')), producer(1024.50)),
                paymentType: $(consumer(regex('^TRANSFER_(OFFLINE|ONLINE)$')), producer('TRANSFER_ONLINE')),
                dueDate    : $(consumer(anyDateTime()))
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