package com.bank.customer_service.service;

import com.bank.customer_service.dto.CustomerRegistrationRequest;
import com.bank.customer_service.entity.Address;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public UUID createCustomerProfile(CustomerRegistrationRequest request) {

        log.info("Rozpoczęto tworzenie profilu dla użytkownika (AuthID): {}", request.authId());

        if (customerRepository.existsByAuthId(request.authId())) {
            log.error("Profil dla AuthID {} już istnieje!", request.authId());
            throw new IllegalStateException("Profil dla tego konta został już utworzony.");
        }
        if (customerRepository.existsByPesel(request.pesel())) {
            log.error("Próba rejestracji na istniejący PESEL: {}", request.pesel());
            throw new IllegalStateException("Klient z podanym numerem PESEL już figuruje w systemie.");
        }

        Address address = Address.builder()
                .street(request.street())
                .buildingNumber(request.buildingNumber())
                .apartmentNumber(request.apartmentNumber())
                .city(request.city())
                .zipCode(request.zipCode())
                .country(request.country())
                .build();


        Customer customer = Customer.builder()
                .authId(request.authId())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .pesel(request.pesel())
                .phoneNumber(request.phoneNumber())
                .address(address)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Pomyślnie utworzono profil klienta. Wewnętrzne ID: {}", savedCustomer.getId());
        return savedCustomer.getId();
    }

}
