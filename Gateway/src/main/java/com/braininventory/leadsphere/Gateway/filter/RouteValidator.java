package com.braininventory.leadsphere.Gateway.filter;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Predicate;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class RouteValidator {

    // List of endpoints that DO NOT need a token
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/eureka"
    );

    public Predicate<HttpServletRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getRequestURI().contains(uri));
}