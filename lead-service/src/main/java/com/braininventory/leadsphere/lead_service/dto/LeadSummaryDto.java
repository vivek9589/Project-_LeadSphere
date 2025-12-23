package com.braininventory.leadsphere.lead_service.dto;


import lombok.Data;

@Data
public class LeadSummaryDto {
    private Long totalLeads;
    private Long convertedLeads;
    private Double conversionRate;


}
