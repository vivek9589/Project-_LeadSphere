package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.*;
import com.braininventory.leadsphere.lead_service.service.LeadDashboardService;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@RestController
@Slf4j
@RequestMapping("/lead")
public class LeadController {

    private final LeadService leadService;

    @Autowired
    LeadDashboardService leadDashboardService;

    @Autowired
    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<LeadResponseDto>> createLead(@RequestBody LeadRequestDto dto) {
        LeadResponseDto data = leadService.createLead(dto);
        return ResponseEntity.ok(ApiResponse.success("Lead created successfully", data));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDto>> getLeadById(@PathVariable Long id) {
        LeadResponseDto data = leadService.getLeadById(id);
        return ResponseEntity.ok(ApiResponse.success("Lead retrieved successfully", data));
    }

    @GetMapping("/getAll")
    public ResponseEntity<ApiResponse<List<LeadResponseDto>>> getAllLeads() {
        List<LeadResponseDto> data = leadService.getAllLeads();
        return ResponseEntity.ok(ApiResponse.success("Leads retrieved successfully", data));
    }

    // Use PatchMapping for partial updates, or PutMapping for full updates
    @PutMapping("/update/{id}")
    public ResponseEntity<StandardResponse<LeadResponseDto>> updateLead(
            @PathVariable Long id,
            @Valid @RequestBody LeadRequestDto dto) {

        LeadResponseDto data = leadService.updateLead(id, dto);
        return ResponseEntity.ok(StandardResponse.ok(data, "Lead updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Best practice for delete operations
    public ResponseEntity<StandardResponse<LeadResponseDto>> deleteLead(@PathVariable Long id) {

        LeadResponseDto data = leadService.deleteLeadById(id);
        return ResponseEntity.ok(StandardResponse.ok(data, "Lead deleted successfully"));
    }

    @GetMapping("/getleadByOwner/{id}")
    public ResponseEntity<StandardResponse<List<LeadResponseDto>>> getLeadsByOwnerId(
            @PathVariable Long id, HttpServletRequest request) {

        log.info("Fetching leads for ownerId: {}", id);
        List<LeadResponseDto> leads = leadService.getLeadsByOwnerId(id);

        if (leads.isEmpty()) {
            log.warn("No leads found for ownerId {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(StandardResponse.error("No leads found", null, request.getRequestURI()));
        }

        log.debug("Found {} leads for ownerId {}", leads.size(), id);
        StandardResponse<List<LeadResponseDto>> response =
                StandardResponse.ok(leads, "Leads fetched successfully");
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }




    @GetMapping("/internal/stats")
    public ResponseEntity<StandardResponse<LeadDashboardResponse>> getInternalStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(required = false) String owner) {

        return ResponseEntity.ok(StandardResponse.ok(
                leadDashboardService.getFilteredDashboard(start, end, owner),
                "Data fetched successfully"));
    }


}