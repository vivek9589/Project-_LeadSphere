package com.braininventory.leadsphere.analytics_service.service;

import com.braininventory.leadsphere.analytics_service.dto.AnalyticsRequest;
import com.braininventory.leadsphere.analytics_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.analytics_service.dto.UserPerformanceDashboardResponseDTO;

public interface AnalyticsService {

    public LeadDashboardResponse getDashboard(String range, String ownerFilter, boolean isAdmin);


    UserPerformanceDashboardResponseDTO getUserDashboard(String range, Long userId);
}
