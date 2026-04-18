package com.bank.card_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CardServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardServiceApplication.class, args);
	}

}
/*
TODO: Card-Service

	-wysyłanie maila o stworzeniu karty trzeba przekierować do notification i on dopieor ywysła maila


		[ ] 1. CQRS — Pobieranie karty (GET)
    - Utworzyć GetCardUseCase
    - Dodać CardQueryRepository (lub użyć istniejącego)
    - Zaimplementować wyszukiwanie karty po ID
    - Zamaskować CVV w DTO (np. "***")
    - Dodać endpoint GET /api/cards/{id}

		[ ] 2. Cykl życia karty — Aktywacja / Blokada (PATCH)
    - Dodać CardStatusUpdateUseCase
    - Dodać walidację przejść statusów:
CREATED -> ACTIVE
ACTIVE -> BLOCKED
BLOCKED -> (nic)
		- Dodać endpoint PATCH /api/cards/{id}/status
    - Zwracać 400 przy nieprawidłowej zmianie statusu

[ ] 3. Odporność na awarie — CustomerProviderAdapter
    - Owinąć wywołanie w try/catch
		- W przypadku błędu:
		- zapisać tymczasowy email: "brak@danych.pl"
		- wysłać event na Kafkę: "customer-data-missing"
		- Dodać logowanie błędów

		[ ] 4. Bezpieczeństwo wejścia — Obsługa błędnego JSON
    - W GlobalExceptionHandler dodać obsługę:
HttpMessageNotReadableException
    - Zwracać 400 z komunikatem:
		"Nieprawidłowy format żądania"
		- Dodać testy dla uszkodzonego JSON-a
*/