package com.bank.card_service.infrastructure.security;

import com.bank.card_service.application.port.out.PinHasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2PinHasher implements PinHasher {

    private final Argon2PasswordEncoder encoder;

    public Argon2PinHasher(
            @Value("${bank.security.argon2.salt-length}") int saltLength,
            @Value("${bank.security.argon2.hash-length}") int hashLength,
            @Value("${bank.security.argon2.parallelism}") int parallelism,
            @Value("${bank.security.argon2.memory}") int memory,
            @Value("${bank.security.argon2.iterations}") int iterations
    ) {
        this.encoder = new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    @Override
    public String hash(String rawPin) {
        return encoder.encode(rawPin);
    }
}
