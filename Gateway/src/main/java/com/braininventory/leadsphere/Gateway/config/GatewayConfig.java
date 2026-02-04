package com.braininventory.leadsphere.Gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.web.servlet.function.RequestPredicates.path;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;



@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("auth_service")
                // Change 1: Use the Service ID as registered in Eureka (usually uppercase)
                // Change 2: Use the .filter(lb()) to enable service discovery lookup
                .route(path("/api/auth/**"), http())
                .filter(lb("jwt-auth-service"))
                .build()

                .and(route("lead_service")
                        .route(path("/lead/**"), http())
                        .filter(lb("lead-service"))
                        .build())

                .and(route("user_service")
                        .route(path("/sales-user/**"), http())
                        .filter(lb("user-service"))
                        .build())

                .and(route("analytics_service")
                        .route(path("/analytics/**"), http())
                        .filter(lb("analytics-service"))
                        .build());
    }

//
//    @Bean
//    @Profile("dev")
//    public WebMvcConfigurer devCorsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:5173", "http://localhost:3000", "http://192.168.29.198:5173","http://92.4.69.110:8080")
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                        .allowedHeaders("*")
//                        .allowCredentials(true);
//            }
//        };
//    }
//
//    @Bean
//    @Profile("prod")
//    public WebMvcConfigurer prodCorsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://localhost:5173", "http://localhost:3000", "http://192.168.29.198:5173","http://92.4.69.110:8080")
//                        //.allowedOrigins("https://your-production-app.com") http://92.4.69.110:8080
//                       // .allowedOrigins("http://92.4.69.110:8080")
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowCredentials(true);
//            }
//        };
//    }
//


}

