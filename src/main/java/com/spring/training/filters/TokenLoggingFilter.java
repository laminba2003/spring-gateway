package com.spring.training.filters;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class TokenLoggingFilter extends AbstractGatewayFilterFactory<TokenLoggingFilter.Config> {

    public TokenLoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (config.isEnabled()) {
                log.info("Pre TokenLoggingFilter : logging the Bearer Token");
                log.info(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0));
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isEnabled()) {
                    log.info("Post TokenLoggingFilter logging");
                }
            }));
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled");
    }

    @Data
    public static class Config {
        boolean enabled = true;
    }
}
