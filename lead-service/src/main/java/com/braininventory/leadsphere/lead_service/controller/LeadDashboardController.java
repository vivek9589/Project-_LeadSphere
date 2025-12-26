package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.lead_service.dto.StandardResponse;
import com.braininventory.leadsphere.lead_service.exception.DashboardException;
import com.braininventory.leadsphere.lead_service.service.LeadDashboardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lead")
@RequiredArgsConstructor
public class LeadDashboardController {

    private final LeadDashboardService dashboardService;

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
}