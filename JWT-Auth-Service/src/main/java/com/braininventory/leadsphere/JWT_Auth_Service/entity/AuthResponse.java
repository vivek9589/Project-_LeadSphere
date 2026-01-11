package com.braininventory.leadsphere.JWT_Auth_Service.entity;


import lombok.Data;

@Data
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn; // seconds for access token
    // getters and setters
}

