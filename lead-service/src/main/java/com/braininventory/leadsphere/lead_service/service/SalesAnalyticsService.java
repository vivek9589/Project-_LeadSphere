package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.lead_service.dto.SalesPerformanceDTO;
import com.braininventory.leadsphere.lead_service.dto.UserPerformanceDashboardResponseDTO;

import java.time.LocalDate;

public interface SalesAnalyticsService {

    SalesPerformanceDTO calculateUserPerformanceMetrics(Long userId);

    UserPerformanceDashboardResponseDTO getConsolidatedUserDashboard(LocalDate start, LocalDate end, Long ownerId);
}
