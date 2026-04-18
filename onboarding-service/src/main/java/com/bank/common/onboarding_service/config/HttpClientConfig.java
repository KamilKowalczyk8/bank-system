package com.bank.common.onboarding_service.config;

import com.bank.common.onboarding_service.client.AccountServiceClient;
import com.bank.common.onboarding_service.client.AuthServiceClient;
import com.bank.common.onboarding_service.client.CustomerServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public AccountServiceClient accountServiceClient(@Value("${services.account.url}") String url) {
        return createClient(AccountServiceClient.class, url);
    }

    @Bean
    public AuthServiceClient authServiceClient(@Value("${services.auth.url}") String url) {
        return createClient(AuthServiceClient.class, url);
    }

    @Bean
    public CustomerServiceClient customerServiceClient(@Value("${services.customer.url}") String url) {
        return createClient(CustomerServiceClient.class, url);
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
