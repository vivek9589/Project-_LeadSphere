package com.braininventory.leadsphere.lead_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadStatsDto {
    private int totalLeads;
    private int convertedLeads;
    private int conversionRate; // percentage
}


