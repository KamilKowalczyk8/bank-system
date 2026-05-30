package com.bank.audit_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.ai.openai.api-key=mock-key-for-ci-pipeline"
})
class AuditServiceApplicationTests {
	@Test
	void contextLoads() {
	}
}
