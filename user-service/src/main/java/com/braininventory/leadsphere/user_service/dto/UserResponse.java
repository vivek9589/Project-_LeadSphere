package com.braininventory.leadsphere.user_service.dto;

import com.braininventory.leadsphere.user_service.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id; // This will map to your customId (e.g., admin_001)
    private String firstName;
    private String lastName;
    private String email;
    private String phone; // Maps from mobileNo
    private Role role;

    private String avatar;
    private String bio;
    private Boolean isActive;

    private AddressDto address;
    private CompanyDto company;
    private SocialLinksDto social;

    private LocalDateTime createdAt;
}