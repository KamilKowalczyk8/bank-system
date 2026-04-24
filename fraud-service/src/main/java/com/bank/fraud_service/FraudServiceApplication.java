package com.bank.fraud_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bank.fraud_service", "com.bank.common"})

public class FraudServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudServiceApplication.class, args);
	}

}
