package com.spring.training.config;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.webflux.ui.SwaggerConfigResource;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
public class SwaggerConfigController {

    final SwaggerConfigResource resource;

    @Operation(hidden = true)
    @GetMapping("/swagger-config")
    public Map<String, Object> getSwaggerUiConfig(ServerHttpRequest request) {
        return resource.getSwaggerUiConfig(request);
    }

}
