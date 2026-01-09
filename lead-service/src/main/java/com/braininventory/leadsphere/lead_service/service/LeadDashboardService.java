package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.dto.LeadDashboardResponse;

import java.time.LocalDate;

public interface LeadDashboardService {

    LeadDashboardResponse getLeadDashboard();
    LeadDashboardResponse getFilteredDashboard(LocalDate start, LocalDate end, Long ownerId);

    // methods for sales-user dashboard


}
