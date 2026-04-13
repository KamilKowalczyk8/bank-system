# Bank System — Microservices Architecture

Projekt zaawansowanego systemu bankowego opartego na architekturze mikroserwisowej. 
Repozytorium zawiera kompletny ekosystem serwisów oraz infrastrukturę potrzebną do uruchomienia i testowania całego procesu onboardingowego.

---

## 🚀 Aktualny stan projektu

System przeszedł transformację z pojedynczego serwisu w pełni funkcjonalną architekturę rozproszoną:

- **api-gateway** - Centralna brama wejściowa (Routing MVC), jedyny punkt styku dla klienta.
- **auth-service** - Zarządzanie tożsamością, logowanie MFA i bezpieczeństwo (Argon2).
- **customer-service** - Zarządzanie danymi osobowymi i profilami klientów.
- **onboarding-service** - Orkiestrator procesu zakładania konta realizujący wzorzec **Saga**.
- **docker-compose** - Pełna infrastruktura: PostgreSQL (osobne instancje), MongoDB, Redis, Kafka, Mailhog.
- **account-service** - System księgi głównej (Zarządzanie saldem i rachunkami).
- **payment-service** - Procesowanie przelewów i koordynacja płatności.
- **fraud-service** - Silnik analityczny oceny ryzyka (Anti-Fraud / Compliance).
- **card-service** - Moduł zarządzania kartami płatniczymi.
- **notification-service** - Asynchroniczna wysyłka powiadomień email(Kafka Consumer).
- **ai-analyzer-service** - Moduł analizujący Errory i Warny w logach poszczególnych serwisów oraz znajduje rozwiązanie tych problemów za pomocą modelu AI postawionego na linuxie.

---

## 🛠 Technologie

- **Java 21** & **Spring Boot 4**
- **Spring Cloud Gateway MVC** (Centralny Routing)
- **Spring Cloud OpenFeign** (Komunikacja między serwisami)
- **Apache Kafka** (Komunikacja asychroniczna)
- **Spring Security** (Argon2, JWT)
- **PostgreSQL 16** & **MongoDB 7** 
- **Redis 7** & **Kafka 7.5 confluent**
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

### 5. Account-Service (Księga główna)
**Port:** `8084` | **Baza:** PostgreSQL (`accountdb`)
* Przechowuje informacje o aktywnych rachunkach i ich saldach.
* Udostępnia API do blokowania/rezerwacji środków podczas procesowania przelewów.

### 6. Payment-Service (Transakcje)
**Port:** `8088` | **Baza:** PostgreSQL (`paymentdb`)
* Tworzenie zleceń płatniczych (INITIATED,PENDING po wcześniejszej weryfikacji fraud oraz rezerwacji środków)
* Komunikacja z fraud-service oraz account-service orkiestracja procesu.
* Po udanym zleceniu płatności emituje asynchroniczne zdarzenie za pomocą Kafki na maila z informacjami o przelewie

### 7. Fraud-Service (Silnik Ryzyka)
**Port:** `8089` | **Baza:** PostgreSQL (`frauddb`)
* Implementacja silnika reguł (Rule Engine). Aktualne reguły: HighAmountRule - transakcje powyżej progu kwotowego, BlacklistedAccountRule - Blokuje konta z globalnej czarnej listy
* Audit Trail: Odłożenie w relacyjnej bazie logów każdego odrzuconego przelewu.

### 8. Notification, AI-Analyzer, Card Services
**Port:** `8085,8086,8087` | **Baza:** PostgreSQL (`carddb`)
* notification-service: Odpowiada za wysyłkę powiadomień (Push/Email/SMS) o zdarzeniach (np. "Przelew zaksięgowany", "Blokada ze względów bezpieczeństwa").
* card-service: Zarządzanie fizycznymi i wirtualnymi nośnikami płatniczymi.
* ai-analyzer-service: Przetwarzanie błędów z logów w tle i anlizowanie ich za pomocą modelu AI.

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

* `POST /auth/register` - Rejestracja (zwraca NIK i tymczasowe hasło).
* `POST /auth/login/step1` - Weryfikacja loginu i inicjalizacja sesji.
* `POST /auth/login/step2` - Weryfikacja hasła i generowanie kodu SMS.
* `POST /auth/login/step3` - Walidacja 2FA i wydanie ostatecznych tokenów JWT.
* `POST /auth/refresh` - Bezpieczne odświeżenie sesji (rotacja).
* `POST /auth/logout` - Unieważnienie sesji użytkownika.

#### 💾 Struktura Bazy Danych
* `users` - centralna tabela poświadczeń (login, hash Argon2, nr telefonu do 2FA, status konta, rola).
* `login_sessions` - tymczasowe zarządzanie procesem logowania (kody SMS, licznik błędnych prób, czas wygaśnięcia sesji).
* `refresh_tokens` - historia i status wydanych tokenów odświeżających (flagi wygaśnięcia i unieważnienia).
* `login_attempts` - historia i prób logowania tych pozytywnych jak i negatywnych.
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

* `POST /api/customers/profile` - Przyjęcie zwalidowanego DTO i utworzenie pełnego profilu klienta wraz z adresem.
* `GET /api/customers/profile/{authId}` - Pobiera pełne dane klienta (profil + adres) na podstawie identyfikatora logowania.
* `DELETE /api/customers/{customerId}` - Fizycznie usuwa profil klienta. Operacja krytyczna, wykorzystywana przez onboarding-service do transakcji kompensacyjnych (Saga Rollback).

#### 💾 Struktura Bazy Danych
* `customers` - główna tabela profilowa (auth_id, PESEL, imię, nazwisko, e-mail, telefon, daty utworzenia/aktualizacji).
* `addresses` - tabela powiązana relacją One-To-One z klientem (ulica, numer budynku, numer lokalu, kod pocztowy, miasto, kraj).

---

### 3. Onboarding-Service (Orkiestrator / Dyrygent)
**Port:** `8083` | **Baza danych:** Brak (Serwis bezstanowy)

Serwis pełniący rolę Dyrygenta (Orchestrator) w systemie. Nie przechowuje własnych danych, ale zarządza skomplikowanym procesem biznesowym ("One-Click Registration"), który wymaga synchronicznej komunikacji z wieloma mikroserwisami. 

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Zarządzanie Transakcją Rozproszoną (Saga):** Sekwencyjne koordynowanie zadań - od wygenerowania loginu, przez utworzenie konta w module Auth, aż po zapisanie danych osobowych w module Customer.
* **Mechanizm Kompensacyjny (Rollback):** Automatyczna naprawa systemu w przypadku błędu. Jeśli utworzenie profilu klienta się nie powiedzie (np. duplikat PESEL), serwis bezbłędnie wysyła żądanie usuwające nowo powstałe konto w `auth-service`, zapobiegając tworzeniu w bazie "kont-zombie".
* **Synchroniczna Komunikacja HTTP:** Użycie Spring Cloud OpenFeign do tworzenia deklaratywnych klientów REST dla komunikacji międzyusługowej.
* **Scentralizowana Obsługa Błędów:** Użycie `@RestControllerAdvice` do przechwytywania wyjątków z innych serwisów i zwracania ujednoliconych obiektów `ErrorResponse` do API Gateway.

#### 🔀 REST API Endpointy
Dokumentacja OpenAPI (Swagger) dostępna pod adresem: `http://localhost:8083/swagger-ui/index.html`

* `POST /api/onboarding/register` - Główny endpoint wyzwalający proces zakładania konta (przyjmuje zagregowany payload z danymi Auth i Customer).

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

* Publiczne
* `POST /auth/**` ➡️ przekierowuje do `Auth-Service`
* `POST /api/onboarding/**` ➡️ przekierowuje do `Onboarding-Service`

* Chronione
* `GET /api/customers/**` ➡️ przekierowuje do `Customer-Service` (Zabezpieczone dodatkowo Circuit Breakerem)
* `POST /api/cards/**` ➡️ przekierowuje do `Card-Service`
* `POST /api/accounts/**` ➡️ przekierowuje do `Account-Service`
* `POST /api/fraud/*` ➡️ przekierowuje do `Fraud-Service`
* `POST /api/payments/**` ➡️ przekierowuje do `Payment-Service`

#### 💾 Struktura Bazy Danych
* *Brak bazy danych.* Routing odbywa się na podstawie reguł trzymanych w pamięci aplikacji.

---

### 5. Account-Service (Księga główna)
**Port:** `8084` | **Baza danych:** PostgreSQL (`accountdb` na porcie `5456`)

System zarządzania rachunkami bankowymi, pełniący rolę głównej księgi rachunkowej. Serwis dba o rygorystyczną spójność sald i jest kluczowym elementem procesowania transakcji finansowych. Oddzielony od danych osobowych (zna tylko ID rachunku), co zwiększa bezpieczeństwo

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Zarządzanie Saldem:** w Czasie Rzeczywistym: Atomowe operacje na środkach finansowych, chroniące przed problemem podwójnego wydatkowania.
* **System Blokad i Rezerwacji:** Udostępnia bezpieczne API dla payment-service do tymczasowego rezerwowania/blokowania środków podczas procesowania przelewów lub płatności kartą.
* **Izolacja Danych:** Całkowity brak informacji o tożsamości klienta (PII). Serwis operuje wyłącznie na parach: UUID konta ↔ Wartość finansowa.

#### 🔀 REST API Endpointy

* `POST /api/accounts/reserve` - Zablokowanie odpowiedniej kwoty na koncie źródłowym na czas procesu weryfikacji transakcji.
* `POST /api/accounts/commit` - Potwierdzenie rezerwacji (fizyczne pobranie środków).
* `POST /api/accounts/rollback` - Zwolnienie zablokowanych środków (np. w przypadku odrzucenia przez fraud-service).
* `GET /api/accounts/{accountId}` - Pobranie aktualnego salda i szczegółów rachunku.


#### 💾 Struktura Bazy Danych
* `accounts` - główna tabela rachunków (id, customer_id, balance, currency, status, created_at).

---

### 6. Payment-Service (Rdzeń Transakcyjny)
**Port:** `8088` | **Baza danych:** PostgreSQL (`paymentdb` na porcie `5459`)

Centralny procesor płatności zaimplementowany w oparciu o zasady Clean Architecture i Porty/Adaptery. Serwis pełni funkcję głównego orkiestratora dla pojedynczego przelewu, łącząc świat synchroniczny (weryfikacje) z asynchronicznym (zdarzenia).

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Maszyna Stanów Płatności:** Rygorystyczne zarządzanie cyklem życia transakcji. Przelew przechodzi przez stany: INITIATED -> PENDING -> COMPLETED / FAILED / REJECTED_FRAUD. Zmiana stanu wymaga autoryzacji z innych serwisów.
* **Orkiestracja Synchroniczna (Feign):** Samodzielna komunikacja z fraud-service (sprawdzenie ryzyka) oraz account-service (rezerwacja środków) przed ostatecznym zatwierdzeniem przelewu.
* **Event-Driven Architecture (Kafka):** o udanym zatwierdzeniu przelewu (stan COMPLETED), serwis staje się Producentem - emituje zdarzenie domenowe PaymentCompletedEvent na magistralę Kafka, nie czekając na reakcję innych systemów.
* **Orkiestracja Synchroniczna (Feign):** Samodzielna komunikacja z fraud-service (sprawdzenie ryzyka) oraz account-service (rezerwacja środków) przed ostatecznym zatwierdzeniem przelewu.


#### 🔀 REST API Endpointy

* `POST /api/payments` - Zgłoszenie intencji przelewu i utworzenie zlecenia ze statusem INITIATED.
* `POST /api/payments/{paymentId}/process` - Wyzwolenie procesu weryfikacji (Fraud) i księgowania (Account), kończące się zmianą statusu na ostateczny.


#### 💾 Struktura Bazy Danych
* `payments` - historia operacji płatniczych (id, source_account_id, destination_account_id, amount, currency, status, type, created_at).

---

### 7. Fraud-Service (Rdzeń Transakcyjny)
**Port:** `8089` | **Baza danych:** PostgreSQL (`frauddb` na porcie `5432`)

Zautomatyzowany Inspektor Bezpieczeństwa (Anti-Fraud / Compliance). Ocenia transakcje w czasie rzeczywistym, działając jako "policjant" przed fizycznym przemieszczeniem środków.
#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Wzorzec Rule Engine:** Modułowa ocena transakcji na podstawie dynamicznie obliczanej punktacji (Risk Score).
* HighAmountRule - Oznacza transakcje powyżej zdefiniowanego progu kwotowego (np. > 10,000 PLN).
* BlacklistedAccountRule - Blokuje operacje (nadaje od razu 100 pkt ryzyka), jeśli konto odbiorcy znajduje się na globalnej czarnej liście.
* **Niezależny Audit Trail:** Zapisywanie tzw. twardych dowodów. Każda odrzucona płatność jest logowana w relacyjnej bazie danych ze szczegółowym powodem (np. lista złamanych reguł) oraz dokładnym wynikiem punktowym.
* **Clean Architecture & Builder:** Warstwa zapisu do bazy jest w pełni odseparowana od logiki biznesowej, a encje audytowe budowane są przy użyciu bezpiecznego wzorca Builder (Lombok), co gwarantuje atomowość tworzenia wpisów.


#### 🔀 REST API Endpointy

* `POST /api/fraud/check` - Główny endpoint decyzyjny. Przyjmuje parametry przelewu i zwraca obiekt decyzji (suspected: true/false, riskScore, reasons).


#### 💾 Struktura Bazy Danych
* `fraud_audit_logs` - rejestr podejrzanych aktywności (id, payment_id, source_account_id, destination_account_id, decision, risk_score, reasons, created_at).

---

### 8. Card-Service (Moduł Zarządzania Kartami Płatniczymi)
**Port:** `8087` | **Baza danych:** PostgreSQL (`carddb` na porcie `5458`)

Zaawansowany technologicznie i kryptograficznie moduł odpowiedzialny za pełen cykl życia wirtualnych i fizycznych kart płatniczych. Architektura serwisu została zaprojektowana w oparciu o Clean Architecture (Porty i Adaptery), co zapewnia całkowitą izolację wrażliwej logiki domenowej od frameworków i infrastruktury zewnętrznej. Serwis implementuje praktyki inspirowane rynkowym standardem bezpieczeństwa.

#### ✨ Główne funkcjonalności i zabezpieczenia:
* **Generowanie Numerów PAN (Standard Luhna):** Zaimplementowany autorski LuhnCardNumberGenerator, który generuje poprawne matematycznie, 16-cyfrowe numery kart kredytowych, zapobiegając literówkom na poziomie algorytmicznym.
* **Kryptograficzne CVV (HMAC):** HmacCvvGenerator odpowiedzialny za wyliczanie 3-cyfrowych kodów bezpieczeństwa. Zgodnie z dobrymi praktykami bezpieczeństwa, kod CVV jest zwracany w postaci jawnej tylko jeden raz (podczas generowania karty w CreateCardResult), co uniemożliwia jego późniejszy wyciek z bazy danych.
* **Bezpieczny PIN (Argon2):** Zastosowanie klasy Argon2PinHasher do jednokierunkowego hashowania kodów PIN, co zabezpiecza je przed atakami typu brute-force i wyciekami bazy danych (podejście analogiczne do haseł w auth-service).
* **Integracja Hybrydowa (REST + Kafka):** Pobieranie i weryfikacja danych klienta "w locie" za pomocą CustomerFeignClient.
* Publikacja zdarzeń domenowych (CardCreatedEvent) za pomocą własnego CardEventProducer na odpowiednie topiki Kafki (nasłuchiwane przez notification-service).
* **Maszyna Stanów Karty:** Ścisła kontrola statusu nośnika (np. karta o statusie CREATED musi zostać najpierw aktywowana przez endpoint /activate, zanim będzie mogła brać udział w transakcjach).


#### 🔀 REST API Endpointy

* `POST /api/cards` - Inicjuje utworzenie nowej wirtualnej karty. Generuje numer Luhna, hash PIN-u oraz kryptograficzne CVV. W odpowiedzi zwraca zmapowane DTO (CardResponse) z jednorazowo widocznym, jawnym kodem CVV.
* `PATCH /api/cards/{id}/activate` - Bezpieczny endpoint transmutujący status logiki domenowej (zmiana z CREATED na ACTIVE).


#### 💾 Struktura Bazy Danych
* `cards` - centralna tabela przechowująca tokeny kart (id, account_id, card_number, hashed_pin, expiration_date, status).

---

### 9. Notification-Service 
**Port:** `8085` | **Baza danych:** brak

* Event Consumer: Nasłuchuje na brokerze (Apache Kafka) na zdarzenia takie jak PaymentCompletedEvent czy FraudDetectedEvent.
* Wysyłka Komunikatów: Odpowiada za generowanie i asynchroniczne wysyłanie powiadomień do użytkowników (Push / E-mail / SMS) na podstawie zdekodowanych payloadów (np. "Twój przelew został zaksięgowany").

---

### 10. AI-Analyzer-Service 
**Port:** `8086` | **Baza danych:** brak

* Analiza Anomalii: Działa w tle, zbierając logi błędów.
* AI Integration: Wykorzystuje model sztucznej inteligencji do analizowania błędów i szybkiego znajdowania dla nich rozwiązań połączony jest z modelem AI postawionym na linuxie.
* Phi-3 Mini: został wybrany głownie przez ograniczenia sprzętowe, a on sam jest ultra lekkim modelem.

---