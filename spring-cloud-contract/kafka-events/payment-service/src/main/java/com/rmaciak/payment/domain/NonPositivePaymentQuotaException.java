package com.rmaciak.payment.domain;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

//@ResponseStatus(value = UNPROCESSABLE_ENTITY)
public class NonPositivePaymentQuotaException extends RuntimeException {
}
