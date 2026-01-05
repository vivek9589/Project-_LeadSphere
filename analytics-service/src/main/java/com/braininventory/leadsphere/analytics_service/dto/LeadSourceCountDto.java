package com.braininventory.leadsphere.analytics_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadSourceCountDto {
    private String source;
    private Long count;
    private String color;
}