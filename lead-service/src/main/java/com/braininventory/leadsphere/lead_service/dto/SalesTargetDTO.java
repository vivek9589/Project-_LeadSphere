package com.braininventory.leadsphere.lead_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesTargetDTO {
    private Double monthlyTarget;
    private Double quarterlyTarget;
    private Integer targetMonth;
    private Integer targetYear;
}