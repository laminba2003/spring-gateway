package com.spring.training.config;

import com.spring.training.filters.LoggingFilter;
import com.spring.training.filters.TokenLoggingGatewayFilterFactory;
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

}