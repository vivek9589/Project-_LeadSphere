package com.braininventory.leadsphere.lead_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadOwnerCountDto {
    private String owner;
    private Long count;
    private String color;

    // Standard constructor for Repository/Stream where color is assigned later
    public LeadOwnerCountDto(String owner, Long count) {
        this.owner = owner;
        this.count = count;
    }
}