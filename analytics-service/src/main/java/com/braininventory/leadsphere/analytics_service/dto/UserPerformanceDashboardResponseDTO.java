package com.braininventory.leadsphere.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPerformanceDashboardResponseDTO {
    private LeadStatsDto leadStats;
    private List<LeadSourceCountDto> leadsBySource;
    private List<LeadSourceCountDto> convertedLeadsBySource;

    // Performance Metrics (Gauge and Bar Chart data)
    private MonthlyAttainmentDTO monthlyAttainment;
    private List<QuarterlyTrendDTO> quarterlyTrend;
}