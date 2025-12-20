package com.braininventory.leadsphere.lead_service.dto;


import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import lombok.Data;

import java.util.Date;

@Data
public class LeadResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private String source;
    private LeadStatus status;
    private String assignedTo;
    private Date createdAt;

    // getters and setters
}
