package com.bank.onboarding_service.dto;

public record CustomerProfileRequest(
        //Customer
        String authId,
        String firstName,
        String lastName,
        String pesel,

        //Address
        String phoneNumber,
        String street,
        String buildingNumber,
        String apartmentNumber,
        String city,
        String zipCode,
        String country
) {}
