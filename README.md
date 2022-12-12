# Spring Cloud Gateway

Spring Cloud Gateway provides a library for building an API Gateway on top of Spring WebFlux. It aims to provide a simple, yet effective way to route to APIs and provide cross cutting concerns to them such as: security, monitoring/metrics, and resiliency.

## Features

Spring Cloud Gateway features:

- Built on Spring Framework 5, Project Reactor and Spring Boot 2.0

- Able to match routes on any request attribute.

- Predicates and filters are specific to routes.

- Circuit Breaker integration.

- Spring Cloud DiscoveryClient integration

- Easy to write Predicates and Filters

- Request Rate Limiting

- Path Rewriting

## The DiscoveryClient Route Definition Locator

You can configure the gateway to create routes based on services registered with a DiscoveryClient compatible service registry.

To enable this, set **spring.cloud.gateway.discovery.locator.enabled=true** and make sure a DiscoveryClient implementation (such as Netflix Eureka, Consul, or Zookeeper) is on the classpath and enabled.

## Setup

### pom.xml

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-consul-discovery</artifactId>
    </dependency>
</dependencies>
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### application.yml

```yaml
spring:
    application:
        name: spring-gateway
    cloud:
      consul:
        host: localhost
        port: 8500
      gateway:
        discovery:
          locator:
            enabled: true

info:
  application:
    name: Spring Cloud Gateway
management:
    endpoint:
      gateway:
        enabled: true
    endpoints:
        web:
          exposure:
            include: health,info,gateway,prometheus
    info:
      env:
        enabled: true
```