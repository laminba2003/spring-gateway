package com.spring.training.config;

import com.spring.training.filters.LoggingFilter;
import com.spring.training.filters.RequestLoggingGatewayFilterFactory;
import com.spring.training.filters.TokenLoggingGatewayFilterFactory;
import org.springdoc.core.GroupedOpenApi;
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
    public GroupedOpenApi springConsul() {
        return createGroupedOpenApi("spring-consul");
    }

    @Bean
    public GroupedOpenApi springConsulClient() {
        return createGroupedOpenApi("spring-consul-webclient");
    }

    private GroupedOpenApi createGroupedOpenApi(String name) {
        return GroupedOpenApi.builder()
                .pathsToMatch("/" + name + "/**")
                .group(name)
                .build();
    }

}