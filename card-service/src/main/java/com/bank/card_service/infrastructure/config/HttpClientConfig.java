package com.bank.card_service.infrastructure.config;

import com.bank.card_service.infrastructure.client.CustomerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public CustomerClient customerClient(@Value("${services.customer.url}") String customerServiceUrl) {

        RestClient restClient = RestClient.builder()
                .baseUrl(customerServiceUrl)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(CustomerClient.class);
    }
}
