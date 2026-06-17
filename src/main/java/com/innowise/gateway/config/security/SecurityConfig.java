package com.innowise.gateway.config.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Value("${security.public-paths}")
  private List<String> publicPaths;

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
      ReactiveJwtTokenFilter jwtTokenFilter) {
    http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .authorizeExchange(exchanges -> {
          AuthorizeExchangeSpec registry = exchanges.pathMatchers(publicPaths.toArray(new String[0])).permitAll();
          registry.anyExchange().authenticated();
        })
        .addFilterAt(jwtTokenFilter, SecurityWebFiltersOrder.AUTHENTICATION);

    return http.build();
  }
}