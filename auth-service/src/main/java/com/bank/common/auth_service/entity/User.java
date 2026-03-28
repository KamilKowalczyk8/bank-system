package com.bank.common.auth_service.entity;

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

    @Column(name = "login", unique = true, nullable = false, length = 8)
    private String login;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "temp_password", nullable = false)
    private boolean tempPassword;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    //--Bezpieczeństwo
    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    //--Audyt
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
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
// 3. Dodać pole isPhoneVerified, jeśli wprowadzimy weryfikację numeru telefonu.
// 4. Dodać metody domenowe (np. recordFailedLogin()), jeśli przejdziemy na pełne DDD.
// 5. Rozważyć przeniesienie logiki blokowania konta do encji, gdy aplikacja urośnie.
// 6. Dodać indeksy w bazie (np. na login), gdy będziemy optymalizować wydajność.
// 7. Dodać soft-delete (np. pole deletedAt), jeśli będziemy usuwać konta użytkowników.

