package com.ashwinsi.api_gateway.Config;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;

@Configuration
public class GatewayConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public RestClientCustomizer gatewayPassThroughCustomizer() {
        // Prevent RestClient from throwing on 4xx/5xx downstream responses.
        // Without this, Spring Framework 6.1+ applies status handlers even in
        // exchange(), causing an exception that triggers Spring Boot's /error
        // dispatch — which swallows the microservice's actual response body.
        return builder -> builder.defaultStatusHandler(
                status -> true,
                (req, res) -> {}
        );
    }
}
