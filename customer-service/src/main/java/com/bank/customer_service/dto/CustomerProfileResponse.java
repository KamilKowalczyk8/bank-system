package com.bank.customer_service.dto;

import java.util.UUID;

public record CustomerProfileResponse(
        UUID customerId,
        String firstName,
        String lastName,
        String pesel,
        String phoneNumber,
        String email,
        String street,
        String buildingNumber,
        String apartmentNumber,
        String city,
        String zipCode,
        String country
) {}
