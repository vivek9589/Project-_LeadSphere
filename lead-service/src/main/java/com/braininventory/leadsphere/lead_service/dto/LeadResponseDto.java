package com.braininventory.leadsphere.lead_service.dto;


import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class LeadResponseDto {
    private Long id;
    private String company;
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String opportunityName;
    private Double value;
    private LeadStatus status;
    private String source;
    private String owner;
    private LocalDateTime  createdAt;
    private LocalDateTime updatedAt;
}