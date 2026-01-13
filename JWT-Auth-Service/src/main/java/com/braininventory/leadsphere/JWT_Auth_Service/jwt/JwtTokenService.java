package com.braininventory.leadsphere.JWT_Auth_Service.jwt;

import com.braininventory.leadsphere.JWT_Auth_Service.service.AuthUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtTokenService {
    // Must be at least 32 characters for HS256 algorithm
    private final String SECRET = "your_super_secret_key_that_is_at_least_32_characters_long";
    private final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Authentication or Username cannot be null");
        }

        // 1. Extract authorities
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // 2. NEW: Extract User ID from AuthUserPrincipal
        Long userId = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthUserDetailsService.AuthUserPrincipal) {
            userId = ((AuthUserDetailsService.AuthUserPrincipal) principal).getId();
        }

        // 3. Build the token with the "id" claim
        try {
            return Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim("id", userId)
                    .claim("authorities", authorities)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 3600000 * 5))
                    .signWith(KEY, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("JWT Generation failed: " + e.getMessage());
        }
    }


    // 2. PARSE: This method is used by your Filter to extract data
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token);
    }

    // 3. VALIDATE: Check if the token matches the user and isn't expired
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parse(token).getBody();
            String username = claims.getSubject();
            boolean isExpired = claims.getExpiration().before(new Date());

            return (username.equals(userDetails.getUsername()) && !isExpired);
        } catch (Exception e) {
            // If parsing fails (tampered or expired), token is invalid
            return false;
        }
    }
}