package contracts.orderService

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Validates that payment API works correctly for offline payment"
    name "should initiate offline payment"
    request {
        method 'PUT'
        urlPath $(consumer(regex('/payment/' + uuid())))
        body([
                accountId  : $(consumer(anyUuid())),
                quota      : $(consumer(regex('^(\\d+\\.\\d+)$')), producer(1024.50)),
                paymentType: 'TRANSFER_OFFLINE',
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
                status           : "IN_PROGRESS",
        ])
        headers {
            contentType('application/json')
        }
    }
}