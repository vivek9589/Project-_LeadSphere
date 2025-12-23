package com.braininventory.leadsphere.Gateway.filter;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Predicate;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/forget-password",
            "/api/auth/reset-password",
            "/eureka"
    );

    public Predicate<HttpServletRequest> isSecured = request -> {
        // FIX: Always permit OPTIONS requests to bypass JWT check
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        return openApiEndpoints.stream()
                .noneMatch(uri -> request.getRequestURI().contains(uri));
    };
}