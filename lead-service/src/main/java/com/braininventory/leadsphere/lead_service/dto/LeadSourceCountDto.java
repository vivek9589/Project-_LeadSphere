package com.braininventory.leadsphere.lead_service.dto;

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

    // Standard constructor for Repository/Stream where color is assigned later
    public LeadSourceCountDto(String source, Long count) {
        this.source = source;
        this.count = count;
    }
}