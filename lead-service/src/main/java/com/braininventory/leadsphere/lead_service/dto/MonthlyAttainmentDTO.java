package com.braininventory.leadsphere.lead_service.dto;

import lombok.Builder;
import lombok.Data;

// MonthlyAttainmentDTO.java
@Data
@Builder
public class MonthlyAttainmentDTO {
    private Double targetValue;
    private Double achievedValue;
    private Double attainmentPercentage;
    private Boolean isOverAchieved;
}