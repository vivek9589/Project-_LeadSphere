package com.braininventory.leadsphere.user_service.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Embeddable
@Data
public class Company {
    private String name; // This is the field in your DB
    private String department;
    private String position;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Joined date must be in YYYY-MM-DD format")
    private String joinedDate;
}