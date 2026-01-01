package com.braininventory.leadsphere.lead_service.controller;


import com.braininventory.leadsphere.lead_service.dto.StandardResponse;
import com.braininventory.leadsphere.lead_service.entity.LeadSource;
import com.braininventory.leadsphere.lead_service.service.LeadSourceService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lead")
@Slf4j
public class LeadSourceController {

    @Autowired
    private LeadSourceService leadSourceService;

    /**
     * API to fetch all active sources for dropdowns
     */
    @GetMapping("/source/getAll")
    @PreAuthorize("hasAnyAuthority('ROLE_SALES_USER', 'ROLE_ADMIN')")
    public ResponseEntity<StandardResponse<List<LeadSource>>> getAllSources(HttpServletRequest request) {
        log.info("Received request to fetch all sources");
        List<LeadSource> sources = leadSourceService.getAllActiveSources();

        return ResponseEntity.ok(
                StandardResponse.ok(sources, "Sources retrieved successfully")
        );
    }

    /**
     * Admin API to add a new dynamic source
     */
    @PostMapping("/source/add")
    @PreAuthorize("hasAnyAuthority('ROLE_SALES_USER', 'ROLE_ADMIN')")
    public ResponseEntity<StandardResponse<LeadSource>> addSource(
            @RequestParam("sourceName") String sourceName, // Replaced Map with Param
            HttpServletRequest request) {

        log.info("Request to add source: {} by user: {}", sourceName, request.getRemoteUser());

        // Service still handles the normalization (trim/uppercase) and duplicate check
        LeadSource savedSource = leadSourceService.addSource(sourceName);

        return new ResponseEntity<>(
                StandardResponse.ok(savedSource, "Source added successfully"),
                HttpStatus.CREATED
        );
    }
}