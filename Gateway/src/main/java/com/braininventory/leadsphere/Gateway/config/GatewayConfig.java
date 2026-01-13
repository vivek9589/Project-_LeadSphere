package com.braininventory.leadsphere.Gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// IMPORTANT: Use the servlet filter, not the reactive one
import org.springframework.context.annotation.Profile;
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

    // 1. DYNAMIC CORS: Active only for 'dev' profile
    @Bean
    @Profile("dev")
    public CorsFilter devCorsFilter() {
        return createCorsFilter(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://192.168.29.198:5173"
        ));
    }

    // 2. DYNAMIC CORS: Active only for 'prod' profile
    @Bean
    @Profile("prod")
    public CorsFilter prodCorsFilter() {
        // Only allow your official production domain
        return createCorsFilter(List.of("https://your-production-app.com"));
    }

    private CorsFilter createCorsFilter(List<String> allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    // 3. CENTRALIZED ROUTING
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
                .and(route("analytics_service")
                        .route(path("/analytics/**"), http())
                        .filter(lb("ANALYTICS-SERVICE"))
                        .build());
    }
}