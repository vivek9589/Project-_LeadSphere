package com.braininventory.leadsphere.JWT_Auth_Service.entity;

import lombok.Data;

import java.util.List;


@Data
public class UserDto {
    private Long id;
    private String username;
    private String passwordHash; // ensure user-service returns hash, never plain text
    private boolean enabled;
    private List<String> roles;
    // getters and setters
}
