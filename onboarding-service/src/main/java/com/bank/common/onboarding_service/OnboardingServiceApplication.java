package com.bank.common.onboarding_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bank.common.onboarding_service", "com.bank.common"})

public class OnboardingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnboardingServiceApplication.class, args);
	}

}
