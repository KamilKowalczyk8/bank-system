package com.bank.document_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootTest(
		classes = DocumentServiceApplication.class,
		properties = {
		"cloud.aws.s3.endpoint=http://localhost:9000",
		"cloud.aws.s3.access-key=dummy-key",
		"cloud.aws.s3.secret-key=dummy-secret",
		"cloud.aws.s3.bucket=dummy-bucket",
		"cloud.aws.s3.region=us-east-1"}
)
@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
})
class DocumentServiceApplicationTests {

	@MockitoBean
	private S3Client s3Client;

	@Test
	void contextLoads() {
	}

}
