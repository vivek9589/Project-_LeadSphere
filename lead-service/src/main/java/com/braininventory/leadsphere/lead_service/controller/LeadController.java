package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.*;
import com.braininventory.leadsphere.lead_service.repository.projections.OwnerFilterProjection;
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
    @PreAuthorize("hasAnyAuthority('ROLE_SALES_USER', 'ROLE_ADMIN')")
    public ResponseEntity<StandardResponse<LeadResponseDto>> deleteLead(@PathVariable Long id) {

        LeadResponseDto data = leadService.deleteLeadById(id);
        return ResponseEntity.ok(StandardResponse.ok(data, "Lead deleted successfully"));
    }

    @GetMapping("/getleadByOwner/{id}")
    public ResponseEntity<StandardResponse<List<LeadResponseDto>>> getLeadsByOwnerId(
            @PathVariable Long id, HttpServletRequest request) {
        log.info("Request received to fetch leads for ownerId: {}", id);

        // Fetch the leads - logic now returns empty list instead of throwing exception if none found
        List<LeadResponseDto> leads = leadService.getLeadsByOwnerId(id);

        // Build the successful response
        StandardResponse<List<LeadResponseDto>> response =
                StandardResponse.ok(leads, leads.isEmpty() ? "No leads found for this owner" : "Leads fetched successfully");

        // Setting path for tracking/logging purposes as per your StandardResponse structure
        response.setPath(request.getRequestURI());

        return ResponseEntity.ok(response);
    }


    @GetMapping("/owners")
    public ResponseEntity<StandardResponse<List<OwnerFilterProjection>>> getUniqueOwners(HttpServletRequest request) {
        log.info("Request received to fetch unique lead owners for filters");

        List<OwnerFilterProjection> owners = leadService.getOwnerFilterList();

        log.info("Successfully retrieved {} unique owners", owners.size());

        return ResponseEntity.ok(
                StandardResponse.ok(owners, "Owner filters retrieved successfully")
        );
    }


//    @GetMapping("/filter")
//    public ResponseEntity<StandardResponse<List<LeadResponseDto>>> searchLeads(
//            @RequestParam(required = false) String contactName,
//            @RequestParam(required = false) String contactEmail,
//            @RequestParam(required = false) String company,
//            @RequestParam(required = false) String opportunityName,
//            HttpServletRequest request) {
//
//        log.info("Searching leads using single filter criteria");
//
//        List<LeadResponseDto> leads = leadService.searchLeadsByFilter(
//                contactName, contactEmail, company, opportunityName);
//
//        StandardResponse<List<LeadResponseDto>> response =
//                StandardResponse.ok(leads, "Search results retrieved successfully");
//        response.setPath(request.getRequestURI());
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ROLE_SALES_USER', 'ROLE_ADMIN')")
    public ResponseEntity<StandardResponse<List<LeadResponseDto>>> searchLeads(
            @RequestParam(name = "query", required = false) String query,
            HttpServletRequest request) {

        log.info("Scoped search initiated for query: {}", query);

        List<LeadResponseDto> leads = leadService.searchLeads(query);

        // 1. Create the response object first
        StandardResponse<List<LeadResponseDto>> response =
                StandardResponse.ok(leads, "Search results retrieved successfully");

        // 2. Set the path (This method returns void, so it must be on its own line)
        response.setPath(request.getRequestURI());

        // 3. Return the fully prepared response
        return ResponseEntity.ok(response);
    }

    @GetMapping("/internal/stats")
    public ResponseEntity<StandardResponse<LeadDashboardResponse>> getInternalStats(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(value = "ownerId", required = false) Long ownerId) {

        log.info("RECEIVED IN LEAD-SERVICE -> ownerId: {}", ownerId); // Check your logs for this!

        return ResponseEntity.ok(StandardResponse.ok(
                leadDashboardService.getFilteredDashboard(start, end, ownerId),
                "Data fetched successfully"));
    }

}