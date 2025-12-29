package com.braininventory.leadsphere.analytics_service.dto;

import lombok.Data;

@Data
public class LeadOwnerCountDto {
    private String owner;
    private Long count;

    // Must be public for JPQL constructor expression
    public LeadOwnerCountDto(String owner, Long count) {
        this.owner = owner;
        this.count = count;
    }
}