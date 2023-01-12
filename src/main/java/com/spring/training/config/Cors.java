package com.spring.training.config;

import lombok.Data;

import java.util.List;

@Data
public class Cors {

    boolean allowCredentials;
    String allowedOriginPattern;
    List<String> allowedHeaders;
    List<String> allowedMethods;

}
