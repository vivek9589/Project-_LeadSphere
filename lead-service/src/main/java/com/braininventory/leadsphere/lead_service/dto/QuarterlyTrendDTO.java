package com.braininventory.leadsphere.lead_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

// QuarterlyTrendDTO.java
@Data
@AllArgsConstructor
public class QuarterlyTrendDTO {

    private String monthName;
    private Double targetValue;
    private Double closedRevenue;
    private Double pipelineValue;
}