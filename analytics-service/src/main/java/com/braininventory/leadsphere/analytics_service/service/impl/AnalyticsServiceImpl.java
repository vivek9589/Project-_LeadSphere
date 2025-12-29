package com.braininventory.leadsphere.analytics_service.service.impl;

import com.braininventory.leadsphere.analytics_service.client.LeadClient;
import com.braininventory.leadsphere.analytics_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.analytics_service.service.AnalyticsService;
import com.braininventory.leadsphere.analytics_service.service.DateRangeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final LeadClient leadClient;
    private final DateRangeCalculator dateCalculator;

    @Override
    public LeadDashboardResponse getDashboard(String range, String ownerFilter, boolean isAdmin) {
        log.info("Generating dashboard for range: {}, owner: {}, isAdmin: {}", range, ownerFilter, isAdmin);

        // 1. Calculate Dates
        LocalDate startDate = dateCalculator.getStartDate(range);
        LocalDate endDate = LocalDate.now();

        // 2. Identity Resolution Logic
        String finalOwner;
        if (isAdmin) {
            // If Admin selects a name, use it. If not, use NULL to see the WHOLE company.
            finalOwner = (ownerFilter != null && !ownerFilter.trim().isEmpty()) ? ownerFilter : null;
        } else {
            // Sales Users are strictly forced to their own identity (Email/Username)
            finalOwner = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        log.info("Requesting Lead Service with finalOwner: {}", finalOwner);

        // 3. Call Lead Service
        try {
            return leadClient.getFilteredStats(startDate, endDate, finalOwner).getData();
        } catch (Exception e) {
            log.error("Communication failure: {}", e.getMessage());
            throw new RuntimeException("Lead Service is currently unreachable");
        }
    }
}