package com.innowise.gateway.config.security;

import com.innowise.commonstarter.security.JwtTokenProvider;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class ReactiveJwtTokenFilter implements WebFilter {

  private final JwtTokenProvider jwtProvider;

  public ReactiveJwtTokenFilter(JwtTokenProvider jwtProvider) {
    this.jwtProvider = jwtProvider;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      String token = authHeader.substring(7);
      return Mono.fromCallable(() -> jwtProvider.validateToken(token))
          .subscribeOn(Schedulers.boundedElastic())
          .flatMap(claims -> {
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            var auth = new UsernamePasswordAuthenticationToken(
                userId, null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
            SecurityContext context = new SecurityContextImpl(auth);
            return chain.filter(exchange)
                .contextWrite(
                    ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
          })
          .onErrorResume(e -> Mono.error(new BadCredentialsException("Invalid token")));
    }
    return chain.filter(exchange);
  }
}