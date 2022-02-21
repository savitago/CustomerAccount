package com.example.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder routeLocatorBuilder) {

        return routeLocatorBuilder.routes()
                .route("customer", rt -> rt.path("/customer/**")
                        .uri("http://localhost:8060/"))
                .route("account-sql", rt -> rt.path("/account/**")
                        .uri("http://localhost:8065/"))
                .build();
    }
}