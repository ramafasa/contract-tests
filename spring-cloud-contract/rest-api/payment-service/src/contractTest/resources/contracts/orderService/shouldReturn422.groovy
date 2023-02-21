package contracts.orderService

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Validates that payment API responds 422 for incorrect payment quota"
    name "should respond 422"
    request {
        method 'PUT'
        urlPath $(consumer(regex('/payment/' + uuid())))
        body([
                accountId  : $(consumer(anyUuid())),
                quota      : $(consumer(regex('^-(\\d+\\.\\d+)')), producer(-40.0)), // negative double
                paymentType: $(consumer(regex('^TRANSFER_(OFFLINE|ONLINE)$')), producer('TRANSFER_ONLINE')),
                dueDate    : $(consumer(anyDateTime()))
        ])
        headers {
            accept 'application/json'
            contentType 'application/json'
        }
    }
    response {
        status UNPROCESSABLE_ENTITY()
        body([
                status           : "FAILED",
        ])
        headers {
            contentType('application/json')
        }
    }
}