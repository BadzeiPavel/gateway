package com.innowise.gateway.config.security;

import com.innowise.gateway.security.ReactiveJwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
      ReactiveJwtTokenFilter jwtTokenFilter) {
    http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(exchanges -> exchanges
            .pathMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
            .pathMatchers("/actuator/health").permitAll()
            .anyExchange().authenticated()
        )
        .addFilterAt(jwtTokenFilter, SecurityWebFiltersOrder.AUTHENTICATION);

    return http.build();
  }
}