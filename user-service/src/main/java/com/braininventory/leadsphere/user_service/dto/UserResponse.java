package com.braininventory.leadsphere.user_service.dto;

import com.braininventory.leadsphere.user_service.enums.Role;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id; // This will map to your customId (e.g., admin_001)
    private String firstName;
    private String lastName;
    private String email;
    //private String countryCode; // e.g., "+91"
    private String phone; // Maps from mobileNo
    private Role role;

    private String avatar;
    private LocalDate dob;
    private Boolean isActive;

    private AddressDto address;
    private CompanyDto company;
    private SocialLinksDto social;

    private LocalDateTime createdAt;
}