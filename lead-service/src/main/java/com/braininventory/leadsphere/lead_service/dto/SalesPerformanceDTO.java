package com.braininventory.leadsphere.lead_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// SalesPerformanceDTO.java
@Data
@AllArgsConstructor
public class SalesPerformanceDTO {
    private MonthlyAttainmentDTO monthlyAttainment;
    private List<QuarterlyTrendDTO> quarterlyTrend;
}