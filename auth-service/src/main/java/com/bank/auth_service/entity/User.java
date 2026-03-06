package com.bank.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean tempPassword;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    //--Bezpieczeństwo
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private Instant lockedUntil;

    private Instant lastLoginAt;

    private Instant passwordChangedAt;

    //--Audyt
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    private Instant updatedAt;


    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.passwordChangedAt == null) {
            this.passwordChangedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}

// TODO (przyszłe rozszerzenia encji User):
// 1. @NotBlank do login, phoneNumber, passwordHash (walidacja aplikacyjna).
// 2. ról użytkownika (np. USER, ADMIN) jeśli pojawi się panel administracyjny.
// 3. Dodać pole isPhoneVerified, jeśli wprowadzimy weryfikację numeru telefonu.
// 4. Dodać metody domenowe (np. recordFailedLogin()), jeśli przejdziemy na pełne DDD.
// 5. Rozważyć przeniesienie logiki blokowania konta do encji, gdy aplikacja urośnie.
// 6. Dodać indeksy w bazie (np. na login), gdy będziemy optymalizować wydajność.
// 7. Dodać soft-delete (np. pole deletedAt), jeśli będziemy usuwać konta użytkowników.

