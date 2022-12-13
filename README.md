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

## The Gateway Metrics Filter
   
To enable gateway metrics, add **spring-boot-starter-actuator** as a project dependency. 
Then, by default, the gateway metrics filter runs as long as the property **spring.cloud.gateway.metrics.enabled** is not set to false. 
This filter adds a timer metric named **spring.cloud.gateway.requests** with the following tags:

- routeId: The route ID.

- routeUri: The URI to which the API is routed.

- outcome: The outcome, as classified by HttpStatus.Series.

- status: The HTTP status of the request returned to the client.

- httpStatusCode: The HTTP Status of the request returned to the client.

- httpMethod: The HTTP method used for the request.

In addition, through the property **spring.cloud.gateway.metrics.tags.path.enabled** (by default, set to false), you can activate an extra metric with the tag:

- path: Path of the request.

These metrics are then available to be scraped from **/actuator/metrics/spring.cloud.gateway.requests** and can be easily integrated with Prometheus to create a Grafana dashboard.

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
      metrics:
        enabled: true
        tags:
          path:
            enabled: true
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
        include: health,info,metrics,gateway,prometheus
  info:
    env:
      enabled: true
```

## TLS and SSL

The gateway can listen for requests on HTTPS by following the usual Spring server configuration. The following example shows how to do so:

### application.yml

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:server.jks
    key-store-password: changeit
    key-store-type: JKS
    key-alias: thinktech
```

You can route gateway routes to both HTTP and HTTPS backends. If you are routing to an HTTPS backend, you can configure the gateway with a set of known certificates that it can trust with the following configuration:


### application.yml

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          trusted-x509-certificates:
            - classpath:server.crt
```