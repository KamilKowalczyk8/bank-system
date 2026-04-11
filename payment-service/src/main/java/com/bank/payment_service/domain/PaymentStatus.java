package com.bank.payment_service.domain;

public enum PaymentStatus {
    INITIATED, //inicjacja
    PENDING, //w trakcie
    REJECTED_FRAUD, //odrzucona przez fraud
    COMPLETED, //sukces
    FAILED //fail
}
