package com.bank.payment_service.infrastructure.config;

import com.bank.payment_service.infrastructure.out.external.account.AccountClient;
import com.bank.payment_service.infrastructure.out.external.fraud.FraudClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public AccountClient accountClient(@Value("${services.account.url}") String accountServiceUrl) {
        return createClient(AccountClient.class, accountServiceUrl);
    }

    @Bean
    public FraudClient fraudClient(@Value("${services.fraud.url}") String fraudServiceUrl) {
        return createClient(FraudClient.class, fraudServiceUrl);
    }

    private <T> T createClient(Class<T> clientClass, String baseUrl) {

        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        return HttpServiceProxyFactory
                .builderFor(adapter)
                .build()
                .createClient(clientClass);
    }
}
