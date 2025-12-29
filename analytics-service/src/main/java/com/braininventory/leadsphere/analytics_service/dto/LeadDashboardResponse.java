package com.braininventory.leadsphere.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeadDashboardResponse {
    private LeadStatsDto leadStats;

    @Builder.Default
    private List<LeadOwnerCountDto> leadsByOwner = new ArrayList<>();

    @Builder.Default
    private List<LeadSourceCountDto> leadsBySource = new ArrayList<>();

    @Builder.Default
    private List<LeadOwnerCountDto> convertedLeadsByOwner = new ArrayList<>();

    @Builder.Default
    private List<LeadSourceCountDto> convertedLeadsBySource = new ArrayList<>();
}