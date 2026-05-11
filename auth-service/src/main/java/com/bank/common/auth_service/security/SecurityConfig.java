package com.bank.common.auth_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${bank.security.argon2.salt-length}")
    private int saltLength;

    @Value("${bank.security.argon2.hash-length}")
    private int hashLength;

    @Value("${bank.security.argon2.parallelism}")
    private int parallelism;

    @Value("${bank.security.argon2.memory}")
    private int memory;

    @Value("${bank.security.argon2.iterations}")
    private int iterations;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                saltLength,
                hashLength,
                parallelism,
                memory,
                iterations
        );
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable)

                    //mówi polegaj tylko na JWT
                    .sessionManagement(session ->
                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/auth/login/step1",
                                    "/auth/login/step2",
                                    "/auth/login/step3",
                                    "/auth/register"
                            ).permitAll()

                            .requestMatchers("/auth/first-password-setup")
                            .hasAuthority("ROLE_PRE_AUTH_PASSWORD_CHANGE")
                            .anyRequest().authenticated()
                    );
            //TODO: wpięcie filtra JWT
            return http.build();
    }
}
