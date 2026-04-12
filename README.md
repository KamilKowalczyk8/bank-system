# Bank System — Microservices Architecture

Projekt zaawansowanego systemu bankowego opartego na architekturze mikroserwisowej. 
Repozytorium zawiera kompletny ekosystem serwisów oraz infrastrukturę potrzebną do uruchomienia i testowania całego procesu onboardingowego.

---

## 🚀 Aktualny stan projektu

System przeszedł transformację z pojedynczego serwisu w pełni funkcjonalną architekturę rozproszoną:

- **api-gateway** — Centralna brama wejściowa (Routing MVC), jedyny punkt styku dla klienta.
- **auth-service** — Zarządzanie tożsamością, logowanie MFA i bezpieczeństwo (Argon2).
- **customer-service** — Zarządzanie danymi osobowymi i profilami klientów.
- **onboarding-service** — Orkiestrator procesu zakładania konta realizujący wzorzec **Saga**.
- **docker-compose** — Pełna infrastruktura: PostgreSQL (osobne instancje), MongoDB.

---

## 🛠 Technologie

- **Java 21** & **Spring Boot 4**
- **Spring Cloud Gateway MVC** (Centralny Routing)
- **Spring Cloud OpenFeign** (Komunikacja między serwisami)
- **Spring Security** (Argon2, JWT)
- **PostgreSQL 16** & **MongoDB 7**
- **Docker / Docker Compose**
- **Lombok** & **Jakarta Validation**

---

## 🏗️ Architektura Systemu

System wykorzystuje wzorzec **Saga (Orkiestracja)** do zarządzania spójnością danych. Podczas rejestracji `onboarding-service` koordynuje działania między `auth-service` a `customer-service`. W przypadku błędu (np. duplikat PESEL), system automatycznie wykonuje **transakcję kompensacyjną** (usuwa konto w Auth), aby nie zostawiać niepełnych danych.

---

## 🛡️ Szczegóły Serwisów

### 1. API Gateway (Brama Systemu)
**Port:** `8080`
Główny punkt wejściowy. Wykorzystuje programowy routing do kierowania żądań do odpowiednich mikrousług, izolując je od bezpośredniego dostępu z sieci zewnętrznej.

### 2. Onboarding Service (Orkiestrator)
**Port:** `8083`
Serwis realizujący logikę biznesową "One-Click Registration". 
* Koordynuje tworzenie poświadczeń i profilu klienta.
* Obsługuje błędy i wycofuje zmiany w przypadku awarii (Saga Rollback).

### 3. Auth-Service (Uwierzytelnianie)
**Port:** `8081` | **Baza:** PostgreSQL (`authdb`)
* Centralne zarządzanie tożsamością (NIK + Hasło).
* Szyfrowanie **Argon2**, mechanizm MFA (2FA), obsługa JWT.

### 4. Customer-Service (Profile)
**Port:** `8082` | **Baza:** PostgreSQL (`customerdb`)
* Przechowywanie danych osobowych, numerów PESEL i adresów.
* Walidacja unikalności i formatu danych (Regex, Jakarta Validation).

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
* **Czyszcenie danych:** Tabela loginsessions jak i refreshtoken są czyszczone w nocy po upłynięciu 7 dni od ich wystąpienia pozwala to pozbywać się zbędnych danych i odciązyć baze z iloscią danych.

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
* `login_attempts` — historia i prób logowania tych pozytywnych jak i negatywnych.
---

### 2. Customer-Service (Mikroserwis Danych Osobowych)
**Port:** `8082` | **Baza danych:** PostgreSQL (`customerdb` na porcie `5455`)

Serwis odpowiedzialny za zarządzanie profilem klienta, przechowywanie danych osobowych (PII) oraz walidację reguł biznesowych. Oddziela dane wrażliwe (tożsamość, adresy) od poświadczeń logowania, wymuszając ścisłą separację w architekturze rozproszonej. 

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Restrykcyjna Walidacja Biznesowa:** Wykorzystanie Jakarta Validation do sprawdzania poprawności danych w locie (m.in. weryfikacja unikalności, Regex dla 9-cyfrowych numerów telefonów, walidacja formatu e-mail).
* **Integralność Danych:** Nałożenie unikalnych więzów (Unique Constraints) na kluczowe pola (PESEL, e-mail) bezpośrednio na poziomie bazy danych.
* **Mapowanie Tożsamości:** Bezpieczne wiązanie danych osobowych z obiektem w `auth-service` za pomocą unikalnego identyfikatora `authId`.
* **Czysta Struktura Danych:** Relacyjny podział na podstawowe dane profilowe i powiązane z nimi dane adresowe.

#### 🔀 REST API Endpointy
Dokumentacja OpenAPI (Swagger) dostępna pod adresem: `http://localhost:8082/swagger-ui/index.html`

* `POST /api/customers/profile` — Przyjęcie zwalidowanego DTO i utworzenie pełnego profilu klienta wraz z adresem.

#### 💾 Struktura Bazy Danych
* `customers` — główna tabela profilowa (auth_id, PESEL, imię, nazwisko, e-mail, telefon, daty utworzenia/aktualizacji).
* `addresses` — tabela powiązana relacją One-To-One z klientem (ulica, numer budynku, numer lokalu, kod pocztowy, miasto, kraj).

---

### 3. Onboarding-Service (Orkiestrator / Dyrygent)
**Port:** `8083` | **Baza danych:** Brak (Serwis bezstanowy)

Serwis pełniący rolę Dyrygenta (Orchestrator) w systemie. Nie przechowuje własnych danych, ale zarządza skomplikowanym procesem biznesowym ("One-Click Registration"), który wymaga synchronicznej komunikacji z wieloma mikroserwisami. 

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Zarządzanie Transakcją Rozproszoną (Saga):** Sekwencyjne koordynowanie zadań – od wygenerowania loginu, przez utworzenie konta w module Auth, aż po zapisanie danych osobowych w module Customer.
* **Mechanizm Kompensacyjny (Rollback):** Automatyczna naprawa systemu w przypadku błędu. Jeśli utworzenie profilu klienta się nie powiedzie (np. duplikat PESEL), serwis bezbłędnie wysyła żądanie usuwające nowo powstałe konto w `auth-service`, zapobiegając tworzeniu w bazie "kont-zombie".
* **Synchroniczna Komunikacja HTTP:** Użycie Spring Cloud OpenFeign do tworzenia deklaratywnych klientów REST dla komunikacji międzyusługowej.
* **Scentralizowana Obsługa Błędów:** Użycie `@RestControllerAdvice` do przechwytywania wyjątków z innych serwisów i zwracania ujednoliconych obiektów `ErrorResponse` do API Gateway.

#### 🔀 REST API Endpointy
Dokumentacja OpenAPI (Swagger) dostępna pod adresem: `http://localhost:8083/swagger-ui/index.html`

* `POST /api/onboarding/register` — Główny endpoint wyzwalający proces zakładania konta (przyjmuje zagregowany payload z danymi Auth i Customer).

#### 💾 Struktura Bazy Danych
* *Brak własnej bazy.* Stan transakcji zarządzany jest w pamięci operacyjnej podczas trwania procesu.

---

### 4. API Gateway (Brama Systemowa)
**Port:** `8080` | **Baza danych:** Brak (Serwis bezstanowy)

Centralna "recepcja" i jedyny punkt styku klienta (aplikacji webowej/mobilnej) z ekosystemem bankowym. Wzorzec ten ukrywa prawdziwą topologię sieci, porty wewnętrznych mikroserwisów oraz upraszcza komunikację frontendu z backendem.

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Silnik Spring Cloud Gateway MVC:** Nowoczesne, synchroniczne podejście oparte na serwerze Tomcat, gwarantujące wysoką kompatybilność i stabilność mapowania żądań.
* **Dynamiczny Routing Programatyczny:** Konfiguracja ścieżek sieciowych w czystej Javie (klasa `GatewayRoutingConfig`) przy użyciu `RouterFunction`, co eliminuje błędy typowe dla plików YAML (np. problemy z białymi znakami) i daje bezpieczeństwo w czasie kompilacji.
* **Izolacja Infrastruktury:** Zewnętrzny klient wysyła wszystkie żądania wyłącznie na port `8080`. Brama bezszelestnie modyfikuje nagłówki i podmienia adresy docelowe na wewnętrzne (`8081`, `8082`, `8083`).
* **Punkt Centralnego Zarządzania:** Gotowe miejsce do przyszłej implementacji globalnych filtrów (np. weryfikacja JWT przed wpuszczeniem do systemu, Rate Limiting, CORS).

#### 🔀 REST API Endpointy
*Serwis nie posiada własnych kontrolerów biznesowych ani interfejsu Swagger. Działa jako Reverse Proxy, udostępniając odpowiednio zmapowane przedrostki ścieżek:*

* `POST /auth/**` ➡️ przekierowuje do `Auth-Service`
* `POST /api/customers/**` ➡️ przekierowuje do `Customer-Service`
* `POST /api/onboarding/**` ➡️ przekierowuje do `Onboarding-Service`

#### 💾 Struktura Bazy Danych
* *Brak bazy danych.* Routing odbywa się na podstawie reguł trzymanych w pamięci aplikacji.
