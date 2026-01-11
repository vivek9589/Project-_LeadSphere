package com.braininventory.leadsphere.JWT_Auth_Service.jwt;


import com.braininventory.leadsphere.JWT_Auth_Service.service.AuthUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService tokenService;
    private final AuthUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenService tokenService, AuthUserDetailsService uds) {
        this.tokenService = tokenService;
        this.userDetailsService = uds;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // ADDED: Skip JWT logic for Swagger and Public Auth paths
        if (path.contains("/v3/api-docs") ||
                path.contains("/swagger-ui") ||
                path.contains("/api/auth/login") ||
                path.contains("/api/auth/forgot-password")) {
            chain.doFilter(req, res);
            return;
        }

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String username = tokenService.parse(token).getBody().getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Re-fetch user from Feign Client via userDetailsService
                var userDetails = userDetailsService.loadUserByUsername(username);

                if (tokenService.isTokenValid(token, userDetails)) {
                    var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    // This line tells Spring: "This user is now authenticated for this specific request"
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        chain.doFilter(req, res);
    }
}

