package com.bank.common.onboarding_service.dto;

public record CustomerProfileRequest(
        //Customer
        String authId,
        String firstName,
        String lastName,
        String pesel,
        String email,

        //Address
        String phoneNumber,
        String street,
        String buildingNumber,
        String apartmentNumber,
        String city,
        String zipCode,
        String country
) {}
