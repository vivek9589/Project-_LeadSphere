package com.braininventory.leadsphere.lead_service.dto;


import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class LeadRequestDto {
    // Remove @NotBlank so these can be null during a partial update
    private String company;
    private String contactName;

    @Email(message = "Invalid email format") // Still validates IF provided
    private String contactEmail;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone")
    private String contactPhone;

    private String opportunityName;

    @PositiveOrZero(message = "Value cannot be negative")
    private Double value;

    private LeadStatus status;
    private String source;
    private String owner;
    private Long ownerId; // Received from the frontend selection
}