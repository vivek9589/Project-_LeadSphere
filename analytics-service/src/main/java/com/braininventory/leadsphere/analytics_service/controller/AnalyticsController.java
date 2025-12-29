package com.braininventory.leadsphere.analytics_service.controller;

import com.braininventory.leadsphere.analytics_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.analytics_service.dto.StandardResponse;
import com.braininventory.leadsphere.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/admin/dashboard")
    public ResponseEntity<StandardResponse<LeadDashboardResponse>> getAdminDashboard(
            @RequestParam(defaultValue = "this_month") String range,
            @RequestParam(required = false) String ownerName) {

        LeadDashboardResponse data = analyticsService.getDashboard(range, ownerName, true);
        return ResponseEntity.ok(StandardResponse.ok(data, "Admin dashboard data retrieved"));
    }

    @GetMapping("/sales-user/dashboard")
    public ResponseEntity<StandardResponse<LeadDashboardResponse>> getUserDashboard(
            @RequestParam(defaultValue = "this_month") String range) {

        LeadDashboardResponse data = analyticsService.getDashboard(range, null, false);
        return ResponseEntity.ok(StandardResponse.ok(data, "User dashboard data retrieved"));
    }
}