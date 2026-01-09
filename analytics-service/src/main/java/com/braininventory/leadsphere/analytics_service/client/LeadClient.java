package com.braininventory.leadsphere.analytics_service.client;

import com.braininventory.leadsphere.analytics_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.analytics_service.dto.StandardResponse;
import com.braininventory.leadsphere.analytics_service.dto.UserPerformanceDashboardResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;


@FeignClient(name = "LEAD-SERVICE")
public interface LeadClient {

    @GetMapping("/lead/internal/stats") // Make sure the path is correct (/lead/internal/stats)
    StandardResponse<LeadDashboardResponse> getFilteredStats(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam("ownerId") Long ownerId // Explicitly name it
    );


    // NEW API: Dedicated for User Dashboard using ID
    // New API for User Dashboard
    @GetMapping("/lead/internal/user-performance")
    StandardResponse<UserPerformanceDashboardResponseDTO> getUserPerformanceStats(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam("ownerId") Long ownerId
    );
}