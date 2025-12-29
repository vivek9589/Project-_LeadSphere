package com.braininventory.leadsphere.lead_service.dto;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    private Boolean isActive;
}
