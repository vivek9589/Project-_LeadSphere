package com.braininventory.leadsphere.Gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// IMPORTANT: Use the servlet filter, not the reactive one
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Arrays;
import java.util.List;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

    @Configuration
    public class GatewayConfig {

        // ADDED: Global CORS Filter for MVC Gateway
        @Bean
        public CorsFilter corsFilter() {
            CorsConfiguration config = new CorsConfiguration();

            // 1. ADD BOTH: localhost for local dev and your machine's IP for network access
            config.setAllowedOrigins(List.of(
                    "http://localhost:5173",
                    "http://localhost:3000",
                    "http://192.168.29.198:5173" // Your frontend IP address
            ));

            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
            config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));

            // Required if you send cookies or use "Authorization" headers
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);

            return new CorsFilter(source);
        }



        @Bean
        public RouterFunction<ServerResponse> gatewayRoutes() {
            return route("auth_service")
                    .route(path("/api/auth/**"), http())
                    .filter(lb("JWT-AUTH-SERVICE"))
                    .build()

                    .and(route("lead_service")
                            .route(path("/lead/**"), http())
                            .filter(lb("LEAD-SERVICE"))
                            .build())

                    .and(route("user_service")
                            .route(path("/sales-user/**"), http())
                            .filter(lb("USER-SERVICE"))
                            .build())

                    // ADDED: Analytics Service Route
                    .and(route("analytics_service")
                            .route(path("/analytics/**"), http())
                            .filter(lb("ANALYTICS-SERVICE"))
                            .build());
        }
    }