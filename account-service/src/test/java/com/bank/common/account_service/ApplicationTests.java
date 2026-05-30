package com.bank.common.account_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ApplicationTests.class)
@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
