package com.braininventory.leadsphere.Gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

// IMPORTANT: This import is required for prefixPath
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.prefixPath;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("auth_service")
                .route(path("/auth/**"), http())
                .filter(lb("JWT-AUTH-SERVICE"))
                // This adds /api to the start of the path before sending to the microservice
                .filter(prefixPath("/api"))
                .build()

                .and(route("lead_service")
                        .route(path("/lead/**"), http())
                        .filter(lb("LEAD-SERVICE"))
                        .build())

                .and(route("user_service")
                        .route(path("/sales-user/**"), http())
                        .filter(lb("USER-SERVICE"))
                        .build());
    }
}