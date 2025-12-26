package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.*;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/lead")
public class LeadController {

    private final LeadService leadService;

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

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<LeadSummaryDto>> getLeadSummary() {
        LeadSummaryDto data = leadService.getLeadSummary();
        return ResponseEntity.ok(ApiResponse.success("Summary retrieved successfully", data));
    }

    @GetMapping("/leadsByOwner")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<LeadOwnerCountDto> getleadsByOwner() {
        return leadService.getLeadsByOwner();

    }


}