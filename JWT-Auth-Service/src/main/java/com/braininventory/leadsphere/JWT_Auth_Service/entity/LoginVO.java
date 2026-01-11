package com.braininventory.leadsphere.JWT_Auth_Service.entity;


import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class LoginVO {

    private Long id;              // optional, useful if you want to track user ID
    private String email;         // username/email
    private String password;  // store BCrypt hash, not plain text
    private String role;            // enum with USER, ADMIN, etc.
    private Set<String> permissions;
    private boolean enabled;      // true if account is active
}
