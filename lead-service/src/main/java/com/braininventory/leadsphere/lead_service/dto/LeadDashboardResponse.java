package com.braininventory.leadsphere.lead_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LeadDashboardResponse {
    private LeadStatsDto leadStats;
    private List<LeadOwnerCountDto> leadsByOwner;
    private List<LeadSourceCountDto> leadsBySource;
    private List<LeadOwnerCountDto> convertedLeadsByOwner;
    private List<LeadSourceCountDto> convertedLeadsBySource;
}
