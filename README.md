# Examples of Contract Tests

This repository contains example usage of contract tests using two frameworks:
* Spring Cloud Contract (https://spring.io/projects/spring-cloud-contract)
* Pact (https://docs.pact.io/)

## Application

Both projects use very simple application to show how to test communication using contract testing.
This application consists of two services:
* payment-service, which exposes the REST API (producer)
* order-service, which is a consumer of the REST API (consumer)

### Request
<img src="https://github.com/ramafasa/contract-tests/blob/main/docs/request.png" width="500" height="250">

### Response
<img src="https://github.com/ramafasa/contract-tests/blob/main/docs/response.png" width="500" height="250">


## [Contract testing with Spring Cloud Contract](https://github.com/ramafasa/contract-tests/tree/main/spring-cloud-contract/payment-service)

Contracts of REST API are located in `/src/contractTest/resources/contracts/orderService` in [payment-service repository](https://github.com/ramafasa/contract-tests/tree/main/spring-cloud-contract/payment-service/src/contractTest/resources/contracts/orderService)

To generate the tests and stub you should ececute Gradle's build:
```
./gradlew build
```

Executing this command generates:
* The test which confirm that REST API of payment-service conforms requirements written down in the contract
* The stub which corresponds to the requirements in the contract

Generated test is located under `/build/generated-test-sources/contractTest/groovy/com/rmaciak/payment/OrderServiceSpec.groovy`

The stub is located under `/build/libs/payment-service-0.0.1-SNAPSHOT-stubs.jar`
  
