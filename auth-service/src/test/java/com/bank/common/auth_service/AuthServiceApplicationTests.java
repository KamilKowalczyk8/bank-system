package com.bank.common.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = AuthServiceApplicationTests.class)
@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
