package com.bank.payment_service.domain;

public enum PaymentStatus {
    INITIATED, //inicjacja
    PENDING, //w trakcie
    COMPLETED, //sukces
    FAILED //fail
}
