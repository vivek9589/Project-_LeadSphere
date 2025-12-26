package com.braininventory.leadsphere.lead_service.dto;

import lombok.Data;


@Data
public class LeadSourceCountDto {
    private String source;
    private Long count;
    private String color;

    public LeadSourceCountDto(String source, Long count) {
        this.source = source;
        this.count = count;
        this.color = mapColor(source); // assign color based on source
    }

    private String mapColor(String source) {
        switch (source) {
            case "web": return "#8B5CF6";
            case "referral": return "#6366F1";
            case "partner / Negotiation": return "#3B82F6";
            case "phone": return "#EC4899";

            default: return "#9CA3AF"; // fallback gray
        }
    }


}