package com.braininventory.leadsphere.lead_service.dto;


import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import lombok.Data;


@Data
public class LeadRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;   // only for creation, not exposed in response
    private String phoneNo;
    private String source;
    private LeadStatus status;
    private String assignedTo;

    // getters and setters
}
