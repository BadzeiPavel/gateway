package com.innowise.gateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  private final SecurityProperties securityProperties;

  public SecurityConfig(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
      ReactiveJwtTokenFilter jwtTokenFilter) {
    String[] publicPathsArray = securityProperties.getPublicPaths().toArray(new String[0]);

    http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers(publicPathsArray).permitAll()
            .anyExchange().authenticated()
        )
        .addFilterAt(jwtTokenFilter, SecurityWebFiltersOrder.AUTHENTICATION);

    return http.build();
  }
}