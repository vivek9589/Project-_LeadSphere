package com.braininventory.leadsphere.analytics_service.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class AnalyticsRequest {
    private String timeRange; // e.g., "today", "week", "month"
    private String ownerName; // Optional filter
}
