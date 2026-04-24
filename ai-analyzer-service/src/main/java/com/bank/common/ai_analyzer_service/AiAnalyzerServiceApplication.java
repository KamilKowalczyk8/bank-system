package com.bank.common.ai_analyzer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bank.common.ai_analyzer_service", "com.bank.common"})
public class AiAnalyzerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiAnalyzerServiceApplication.class, args);
	}

}
