package com.spring.training.config;

import com.spring.training.filters.LoggingFilter;
import com.spring.training.filters.RequestLoggingGatewayFilterFactory;
import com.spring.training.filters.TokenLoggingGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    public TokenLoggingGatewayFilterFactory tokenLoggingGatewayFilterFactory() {
        return new TokenLoggingGatewayFilterFactory();
    }

    @Bean
    public RequestLoggingGatewayFilterFactory requestLoggingGatewayFilterFactory() {
        return new RequestLoggingGatewayFilterFactory();
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> exchange.getPrincipal().map(principal -> principal.getName());
    }

}