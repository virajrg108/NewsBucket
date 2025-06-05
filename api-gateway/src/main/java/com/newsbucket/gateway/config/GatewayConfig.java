package com.newsbucket.gateway.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("news_route", r -> r
                        .path("/api/news/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("newsCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/news"))
                                .rewritePath("/api/(?<segment>.*)", "/api/${segment}")
                                .requestRateLimiter(c -> c
                                        .setRateLimiter(RedisRateLimiter.builder()
                                                .replenishRate(10)
                                                .burstCapacity(20)
                                                .requestedTokens(1)
                                                .build())
                                        .setKeyResolver(this.ipKeyResolver())
                                        .setStatusCode(HttpStatus.TOO_MANY_REQUESTS))
                                .retry(retryConfig -> retryConfig
                                        .setRetries(3)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, 
                                                   HttpStatus.BAD_GATEWAY, 
                                                   HttpStatus.SERVICE_UNAVAILABLE)))
                        .uri("http://localhost:8081"))
                .route("client_route", r -> r
                        .path("/**")
                        .filters(f -> f
                                .circuitBreaker(config -> config
                                        .setName("clientCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/default")))
                        .uri("http://localhost:5173"))
                .route("actuator_route", r -> r
                        .path("/actuator/**")
                        .uri("http://localhost:8080"))
                .build();
    }
    
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress()
                .getAddress().getHostAddress());
    }
}