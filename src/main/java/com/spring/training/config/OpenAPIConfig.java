package com.spring.training.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

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
