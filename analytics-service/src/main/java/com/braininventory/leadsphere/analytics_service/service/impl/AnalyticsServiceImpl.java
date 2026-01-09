package com.braininventory.leadsphere.analytics_service.service.impl;

import com.braininventory.leadsphere.analytics_service.client.LeadClient;
import com.braininventory.leadsphere.analytics_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.analytics_service.dto.StandardResponse;
import com.braininventory.leadsphere.analytics_service.dto.UserPerformanceDashboardResponseDTO;
import com.braininventory.leadsphere.analytics_service.service.AnalyticsService;
import com.braininventory.leadsphere.analytics_service.service.DateRangeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final LeadClient leadClient;
    private final DateRangeCalculator dateCalculator;
    @Override
    public LeadDashboardResponse getDashboard(String range, Long ownerIdFilter, boolean isAdmin) {
        log.info("Generating dashboard for range: {}, ownerId: {}, isAdmin: {}", range, ownerIdFilter, isAdmin);

        LocalDate startDate = dateCalculator.getStartDate(range);
        LocalDate endDate = LocalDate.now();

        Long finalOwnerId;
        if (isAdmin) {
            // Admin uses the passed ID or null for global view
            finalOwnerId = ownerIdFilter;
        } else {
            // 1. Get JWT from SecurityContext
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) auth.getPrincipal();

            // 2. Extract the 'id' claim we added to the JWT earlier
            finalOwnerId = jwt.getClaim("id");
        }

        log.info("Requesting Lead Service with finalOwnerId: {}", finalOwnerId);

        try {
            log.info("Requesting Lead Service with finalOwnerId: {}", finalOwnerId);
            // Ensure finalOwnerId is not being overwritten by a local variable
            return leadClient.getFilteredStats(startDate, endDate, finalOwnerId).getData();
        } catch (Exception e) {
            log.error("Lead Service communication failure: {}", e.getMessage());
            throw new RuntimeException("Lead Service is currently unreachable");
        }
    }

    // user dashboard as new requirement

    @Override
    public UserPerformanceDashboardResponseDTO getUserDashboard(String range, Long userId) {
        log.info("Orchestrating User Performance Dashboard. ID: {}, Range: {}", userId, range);

        // 1. Resolve Time Range (Today, Week, Month, Year, Total)
        LocalDate startDate = dateCalculator.getStartDate(range);
        LocalDate endDate = LocalDate.now();

        // 2. Fetch Consolidated Data via Feign
        try {
            StandardResponse<UserPerformanceDashboardResponseDTO> response =
                    leadClient.getUserPerformanceStats(startDate, endDate, userId);

            return response.getData();
        } catch (Exception e) {
            log.error("Communication failure with Lead Service for User {}: {}", userId, e.getMessage());
            throw new RuntimeException("Lead Service unreachable for User Dashboard");
        }
    }





}