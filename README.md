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

## The Token Relay Gateway Filter 

A Token Relay is where an OAuth2 consumer acts as a Client and forwards the incoming token to outgoing resource
requests. The consumer can be a pure Client (like an SSO application) or a Resource Server.
Spring Cloud Gateway can forward OAuth2 access tokens downstream to the services it is proxying. 
To enable this for Spring Cloud Gateway, add the following dependencies and configurations

For the gateway to act like a pure Client :

- **org.springframework.boot:spring-boot-starter-oauth2-client**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: spring-consul
          uri: lb://spring-consul
          predicates:
            - Path=/resources/**
          filters:
            - StripPrefix=1
            - TokenRelay=
  security:
    oauth2:
      client:
        registration:
          gateway:
            provider: keycloak
            client-id: spring
            client-secret: tkkH2u6lTvxh3JFsaveNQuXPWXFkd4yw
            authorization-grant-type: authorization_code
            redirectUri: https://localhost:8089/login/oauth2/code/gateway
            scope: openid,profile,email
        provider:
          keycloak:
            authorization-uri: http://localhost:8080/realms/training/protocol/openid-connect/auth
            token-uri: http://localhost:8080/realms/training/protocol/openid-connect/token
            user-info-uri: http://localhost:8080/realms/training/protocol/openid-connect/userinfo
            user-name-attribute: sub
            jwk-set-uri: http://localhost:8080/realms/training/protocol/openid-connect/certs

```

For the gateway to act like a Resource Server : 

- **org.springframework.boot:spring-boot-starter-oauth2-resource-server**

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: spring-consul
          uri: lb://spring-consul
          predicates:
            - Path=/resources/**
          filters:
            - StripPrefix=1
            - TokenRelay=
  security:
    oauth2:
      resourceserver:
        jwt:
          jwk-set-uri: http://localhost:8080/realms/training/protocol/openid-connect/certs
```

## TLS and SSL

The gateway can listen for requests on HTTPS by following the usual Spring server configuration. The following example shows how to do so:

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

```yaml
spring:
  cloud:
    gateway:
      httpclient:
        ssl:
          trusted-x509-certificates:
            - classpath:server.crt
```

You can generate your own certificate with the *openssl* tool following these steps:

### Create self-signed certificate

```
openssl req -new -config server.cnf -keyout server.key -out server.csr
openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt -extfile server.cnf -extensions req_ext
```

### Convert the x.509 certificate and key to a pkcs12 file

```
openssl pkcs12 -export -in server.crt -inkey server.key -out server.p12 -name thinktech
```

### Convert the pkcs12 file to a Java keystore

```
keytool -importkeystore -deststorepass changeit -destkeypass changeit -destkeystore server.jks -srckeystore server.p12 -srcstoretype PKCS12 -srcstorepass changeit -alias thinktech
```
