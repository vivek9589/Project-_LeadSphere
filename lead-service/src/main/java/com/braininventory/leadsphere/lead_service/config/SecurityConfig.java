package com.braininventory.leadsphere.lead_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity // Activates @PreAuthorize for role-based access
public class SecurityConfig {

    // Your shared secret key (Must match the one used to sign the token in Auth Service)
    private final String SECRET = "your_super_secret_key_that_is_at_least_32_characters_long";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Required for stateless REST APIs
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder()) // Uses our custom HS256 decoder
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Uses our authorities mapper
                        )
                );
        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Convert the string secret into a SecretKey object for HS256
        SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");

        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256) // Explicitly tell Spring to expect HS256
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

        // 1. Tell Spring to look for the "authorities" claim (instead of "scope")
        authoritiesConverter.setAuthoritiesClaimName("authorities");

        // 2. Keep the prefix empty because your JWT already has "ROLE_" prefixes
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return jwtConverter;
    }
}