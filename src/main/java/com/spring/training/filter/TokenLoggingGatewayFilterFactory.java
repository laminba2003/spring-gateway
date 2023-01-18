package com.spring.training.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TokenLoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenLoggingGatewayFilterFactory.Config> {

    public TokenLoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> exchange.getPrincipal().flatMap(principal -> {
            if (config.isEnabled()) {
                log.debug("Pre TokenLoggingFilter : logging the Bearer Token");
                Optional<List<String>> headers = Optional.ofNullable(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION));
                headers.ifPresent(header -> log.debug(header.get(0)));
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isEnabled()) {
                    log.debug("Post TokenLoggingFilter logging");
                }
            }));
        });
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList("enabled");
    }

    @Data
    public static class Config {
        boolean enabled = true;
    }

}
