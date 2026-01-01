package com.braininventory.leadsphere.user_service.dto;

import com.braininventory.leadsphere.user_service.enums.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateRequest {

    @Size(max = 50, message = "First name is too long")
    private String firstName;

    @Size(max = 50, message = "Last name is too long")
    private String lastName;


    //private String countryCode; // e.g., "+91"

    // @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone format")
    private String phone;

    private LocalDate dob;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;



    @Valid
    private AddressDto address;

    @Valid
    private SocialLinksDto social;

    @Valid
    private CompanyDto company;
}