package com.bank.common.onboarding_service.client;

import com.bank.common.onboarding_service.dto.AuthRegistrationRequest;
import com.bank.common.onboarding_service.dto.AuthResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/auth")
public interface AuthServiceClient {

    @PostExchange("/register")
    AuthResponse registerAccount(@RequestBody AuthRegistrationRequest request);

    @DeleteExchange("/{authId}")
    void deleteAccount(@PathVariable("authId") String authId);
}
