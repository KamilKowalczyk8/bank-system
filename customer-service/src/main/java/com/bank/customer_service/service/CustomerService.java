package com.bank.customer_service.service;

import com.bank.customer_service.dto.CustomerProfileResponse;
import com.bank.customer_service.dto.CustomerRegistrationRequest;
import com.bank.customer_service.entity.Address;
import com.bank.customer_service.entity.Customer;
import com.bank.customer_service.exception.CustomerAlreadyExistsException;
import com.bank.customer_service.exception.CustomerNotFoundException;
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
            throw new CustomerAlreadyExistsException("Profil dla tego konta został już utworzony.");
        }
        if (customerRepository.existsByPesel(request.pesel())) {
            log.error("Próba rejestracji na istniejący PESEL: {}", request.pesel());
            throw new CustomerAlreadyExistsException("Klient z podanym numerem PESEL już figuruje w systemie.");
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
                .email(request.email())
                .address(address)
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Pomyślnie utworzono profil klienta. Wewnętrzne ID: {}", savedCustomer.getId());
        return savedCustomer.getId();
    }

    public void deleteCustomerHard(String customerId) {
        log.warn("Otrzymano żądanie usunięcia konta dla customerId: {}", customerId);

        var profile = customerRepository.findByAuthId(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Nie znaleziono profilu do usunięcia dla ID: " + customerId));

        customerRepository.delete(profile);
        log.info("Konto o ID: {} zostało pomyślnie usunięte z bazy (Saga Rollback).", customerId);
    }

    @Transactional(readOnly = true)
    public CustomerProfileResponse getCustomerProfile(String authId) {
        log.info("Pobieranie profilu dla AuthId: {}", authId);

        Customer customer = customerRepository.findByAuthId(authId)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono profilu dla podanego AuthID: " + authId));

        Address address = customer.getAddress();

        return new CustomerProfileResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPesel(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                address.getStreet(),
                address.getBuildingNumber(),
                address.getApartmentNumber(),
                address.getCity(),
                address.getZipCode(),
                address.getCountry()
        );
    }



}
