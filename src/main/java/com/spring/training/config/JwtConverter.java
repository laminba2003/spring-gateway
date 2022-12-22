package com.spring.training.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.spring.training.config.Claims.ROLES;

public class JwtConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        List<String> roles = Optional.ofNullable(jwt.getClaimAsStringList(ROLES)).orElse(new ArrayList<>());
        List<GrantedAuthority> authorities = roles.stream().
                map(authority -> new SimpleGrantedAuthority("ROLE_" + authority))
                .collect(Collectors.toList());
        OidcIdToken oidcIdToken = new OidcIdToken(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getClaims());
        OidcUser user = new DefaultOidcUser(authorities, oidcIdToken);
        OAuth2AccessToken oAuth2AccessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt());
        return Mono.just(new BearerTokenAuthentication(user, oAuth2AccessToken, authorities));
    }

}