package com.braininventory.leadsphere.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuarterlyTrendDTO {
    private String monthName;
    private Double targetValue;
    private Double closedRevenue;
    private Double pipelineValue;
}