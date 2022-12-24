package com.spring.training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.*;

import static com.spring.training.config.Claims.ROLES;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @Profile("auth")
    public SecurityWebFilterChain oauth2SecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/actuator/**")
                .hasRole("admin")
                .pathMatchers("/**").authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt().jwtAuthenticationConverter(new JwtConverter()))
                .cors().and().csrf().disable();
        return http.build();
    }

    @Bean
    @Profile("auth-client")
    public SecurityWebFilterChain oauth2ClientSecurityFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/actuator/**")
                .hasRole("admin")
                .pathMatchers("/**").authenticated())
                .oauth2Login(oauth2Login -> withDefaults())
                .oauth2Client(oauth2Client -> withDefaults())
                .cors().and().csrf().disable();
        return http.build();
    }

    @Bean
    @Profile("auth-client")
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(grantedAuthority -> {
                if (grantedAuthority instanceof OidcUserAuthority) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) grantedAuthority;
                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
                    List<String> roles = Optional.ofNullable(userInfo.getClaimAsStringList(ROLES)).orElse(new ArrayList<>());
                    roles.stream().map(authority -> new SimpleGrantedAuthority("ROLE_" + authority)).forEach(mappedAuthorities::add);
                }
            });
            return mappedAuthorities;
        };
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.addAllowedOrigin(CorsConfiguration.ALL);
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        source.registerCorsConfiguration("/**", configuration);
        return new CorsWebFilter(source);
    }

}
