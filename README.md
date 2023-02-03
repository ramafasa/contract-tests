# Examples of Contract Tests

This repository contains example usage of contract tests using two frameworks:
* Spring Cloud Contract (https://spring.io/projects/spring-cloud-contract)
* Pact (https://docs.pact.io/)

## Application

Both projects use very simple application to show how to test communication using contract testing.
This application consists of two services:
* payment-service, which exposes the REST API
* order-service, which is a consumer of the REST API 

### Request
<img src="https://github.com/ramafasa/contract-tests/blob/main/docs/request.png" width="500" height="250">

### Response
<img src="https://github.com/ramafasa/contract-tests/blob/main/docs/response.png" width="500" height="250">


## Contract testing with Spring Cloud Contract
