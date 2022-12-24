package com.spring.training.filters;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Slf4j
public class RequestLoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestLoggingGatewayFilterFactory.Config> {

    public RequestLoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new OrderedGatewayFilter((exchange, chain) -> exchange.getPrincipal().flatMap(principal -> {
            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponseDecorator responseDecorator = decorateResponse(request, response, principal);
            log.info("handling request for URI : {} with method : {} and user : {}", request.getURI(), request.getMethod(), principal);
            return chain.filter(exchange.mutate().request(request).response(responseDecorator).build());
        }), -4);
    }

    private ServerHttpResponseDecorator decorateResponse(ServerHttpRequest request, ServerHttpResponse response, Principal principal) {
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(final Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        DefaultDataBuffer buffer = new DefaultDataBufferFactory().join(dataBuffers);
                        byte[] content = new byte[buffer.readableByteCount()];
                        buffer.read(content);
                        String responseBody = new String(content, StandardCharsets.UTF_8);
                        log.info("handling request completed for URI : {} with method : {}, response body : {} and user : {} ", request.getURI(), request.getMethod(), responseBody, principal);
                        DataBufferFactory dataBufferFactory = response.bufferFactory();
                        return dataBufferFactory.wrap(responseBody.getBytes());
                    })).onErrorResume(err -> {
                        log.error("error while handling request for URI : {} with method : {} , error message : {} and user : {}", request.getURI(), request.getMethod(), err.getMessage(), principal);
                        return Mono.empty();
                    });
                }
                return super.writeWith(body);
            }
        };
    }

    @Data
    public static class Config {
    }

}
