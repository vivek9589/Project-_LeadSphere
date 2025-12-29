package com.braininventory.leadsphere.analytics_service.client;

import com.braininventory.leadsphere.analytics_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.analytics_service.dto.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;


@FeignClient(name = "LEAD-SERVICE")
public interface LeadClient {

    @GetMapping("/lead/internal/stats")
    StandardResponse<LeadDashboardResponse> getFilteredStats(
            @RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
            @RequestParam(value = "owner", required = false) String owner
    );
}