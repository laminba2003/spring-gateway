package com.spring.training.config;

import com.spring.training.filters.LoggingFilter;
import com.spring.training.filters.RequestLoggingGatewayFilterFactory;
import com.spring.training.filters.TokenLoggingGatewayFilterFactory;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator, ConfigurableListableBeanFactory beanFactory) {
        return locator.getRouteDefinitions()
                .filter(routeDefinition -> routeDefinition.getId().matches(".*-docs"))
                .map(routeDefinition -> {
                    String name = routeDefinition.getId().replaceAll("-docs", "");
                    System.out.println("name "+name);
                    GroupedOpenApi group = groupedOpenApi(name);
                    return group;
                }).toStream().collect(Collectors.toList());
    }

    @Bean
    public GroupedOpenApi springConsul() {
        return groupedOpenApi("spring-consul");
    }

    @Bean
    public GroupedOpenApi springConsulClient() {
        return groupedOpenApi("spring-consul-webclient");
    }

    public GroupedOpenApi groupedOpenApi(String name) {
        return GroupedOpenApi.builder()
                .pathsToMatch("/" + name + "/**")
                .group(name)
                .build();
    }

}