# Bank System — Microservices Architecture

Projekt systemu bankowego opartego na architekturze mikroserwisowej.  
Repozytorium zawiera wszystkie serwisy oraz infrastrukturę potrzebną do uruchomienia systemu lokalnie.

---

## Aktualny stan projektu

Na tym etapie dostępne są:

- **auth-service** — podstawowa struktura mikroserwisu odpowiedzialnego za rejestrację, logowanie i bezpieczeństwo użytkowników.
- **docker-compose** — konfiguracja PostgreSQL i MongoDB dla Auth Service.

---

## Technologie

- Java 21  
- Spring Boot 3  
- Spring Security (Argon2)  
- PostgreSQL  
- MongoDB  
- Docker / Docker Compose  

---

## Uruchamianie środowiska

W katalogu `docker/`:

```bash
docker compose up -d

```
---

## 🛡️ Architektura Serwisów

### 1. Auth-Service (Mikroserwis Uwierzytelniania)
**Port:** `8081` | **Baza danych:** PostgreSQL (`authdb`)

Serwis odpowiedzialny za centralne zarządzanie tożsamością, bezpieczne logowanie oraz wydawanie i walidację tokenów JWT dla całego ekosystemu bankowego. Zbudowany zgodnie z zasadami Clean Architecture i Single Responsibility Principle (nie przechowuje danych osobowych, a jedynie poświadczenia).

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Silne szyfrowanie:** Wykorzystanie algorytmu **Argon2** do hashowania haseł, chroniącego przed atakami brute-force i słownikowymi (rainbow tables).
* **Wieloetapowe logowanie (MFA / 2FA):**
  * Krok 1: Weryfikacja NIK (8-cyfrowy unikalny login). Chroni przed enumeracją użytkowników.
  * Krok 2: Weryfikacja hasła z mechanizmem blokady konta po 3 nieudanych próbach logowania.
  * Krok 3: Weryfikacja jednorazowego kodu SMS (symulacja) sprzężona z identyfikatorem sesji.
* **Nowoczesne zarządzanie sesją (JWT):**
  * Krótkoterminowe Access Tokeny (5 minut) do autoryzacji żądań HTTP.
  * Długoterminowe Refresh Tokeny (24 godziny) przechowywane w bazie danych.
* **Refresh Token Rotation:** Mechanizm automatycznej wymiany tokenów na nową parę przy każdym odświeżaniu sesji.
* **Tarcza anty-włamaniowa (Replay Attack Prevention):** Detekcja prób ponownego użycia unieważnionego (skradzionego) tokena, skutkująca natychmiastowym spaleniem wszystkich tokenów danego użytkownika (Kill Switch).
* **Wylogowywanie:** Idempotentny endpoint bezpiecznie unieważniający tokeny w bazie bez wycieku informacji o ich stanie.

#### 🔀 REST API Endpointy
Pełna, interaktywna dokumentacja OpenAPI (Swagger) jest dostępna lokalnie po uruchomieniu serwisu pod adresem: `http://localhost:8081/swagger-ui/index.html`

* `POST /auth/register` — Rejestracja (zwraca NIK i tymczasowe hasło).
* `POST /auth/login/step1` — Weryfikacja loginu i inicjalizacja sesji.
* `POST /auth/login/step2` — Weryfikacja hasła i generowanie kodu SMS.
* `POST /auth/login/step3` — Walidacja 2FA i wydanie ostatecznych tokenów JWT.
* `POST /auth/refresh` — Bezpieczne odświeżenie sesji (rotacja).
* `POST /auth/logout` — Unieważnienie sesji użytkownika.

#### 💾 Struktura Bazy Danych
* `users` — centralna tabela poświadczeń (login, hash Argon2, nr telefonu do 2FA, status konta, rola).
* `login_sessions` — tymczasowe zarządzanie procesem logowania (kody SMS, licznik błędnych prób, czas wygaśnięcia sesji).
* `refresh_tokens` — historia i status wydanych tokenów odświeżających (flagi wygaśnięcia i unieważnienia).
