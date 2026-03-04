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
