package com.bank.onboarding_service.client;

import com.bank.onboarding_service.dto.AuthRegistrationRequest;
import com.bank.onboarding_service.dto.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${services.auth.url}")
public interface AuthServiceClient {

    @PostMapping("/auth/register")
    AuthResponse registerAccount(@RequestBody AuthRegistrationRequest request);

    @DeleteMapping("/auth/{authId}")
    void deleteAccount(@PathVariable("authId") String authId);
}
