package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.lead_service.dto.SalesPerformanceDTO;
import com.braininventory.leadsphere.lead_service.dto.StandardResponse;
import com.braininventory.leadsphere.lead_service.dto.UserPerformanceDashboardResponseDTO;
import com.braininventory.leadsphere.lead_service.exception.DashboardException;
import com.braininventory.leadsphere.lead_service.service.LeadDashboardService;
import com.braininventory.leadsphere.lead_service.service.SalesAnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Slf4j
@RequestMapping("/lead")
@RequiredArgsConstructor
public class LeadDashboardController {

    private final LeadDashboardService dashboardService;
    private final SalesAnalyticsService salesAnalyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<StandardResponse<LeadDashboardResponse>> getLeadDashboard(HttpServletRequest request) {
        try {
            LeadDashboardResponse response = dashboardService.getLeadDashboard();
            return ResponseEntity.ok(StandardResponse.ok(response, "Dashboard metrics loaded"));
        } catch (DashboardException ex) {
            // Map our custom exception to a 500 status code with your StandardResponse
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(StandardResponse.error(ex.getMessage(), "DB_CONNECTION_ERROR", request.getRequestURI()));
        }
    }



    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<StandardResponse<SalesPerformanceDTO>> getSalesDashboard(
            @PathVariable Long userId,
            HttpServletRequest request) {

        log.info("Received dashboard request for user: {}", userId);

        SalesPerformanceDTO performanceData = salesAnalyticsService.calculateUserPerformanceMetrics(userId);

        return ResponseEntity.ok(
                StandardResponse.ok(performanceData, "Sales performance metrics retrieved successfully")
        );
    }

    // Change the path here to match your Feign Client
    @GetMapping("/internal/user-performance")
    public ResponseEntity<StandardResponse<UserPerformanceDashboardResponseDTO>> getInternalUserDashboard(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam Long ownerId) {

        UserPerformanceDashboardResponseDTO response = salesAnalyticsService.getConsolidatedUserDashboard(start, end, ownerId);

        return ResponseEntity.ok(StandardResponse.ok(response, "User dashboard metrics retrieved"));
    }

}