package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.ApiResponse;
import com.braininventory.leadsphere.lead_service.dto.LeadRequestDto;
import com.braininventory.leadsphere.lead_service.dto.LeadResponseDto;
import com.braininventory.leadsphere.lead_service.dto.LeadSummaryDto;
import com.braininventory.leadsphere.lead_service.service.LeadService;
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

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDto>> updateLead(@PathVariable Long id, @RequestBody LeadRequestDto dto) {
        LeadResponseDto data = leadService.updateLead(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Lead updated successfully", data));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<LeadResponseDto>> deleteLead(@PathVariable Long id) {
        LeadResponseDto data = leadService.deleteLeadById(id);
        return ResponseEntity.ok(ApiResponse.success("Lead deleted successfully", data));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<LeadSummaryDto>> getLeadSummary() {
        LeadSummaryDto data = leadService.getLeadSummary();
        return ResponseEntity.ok(ApiResponse.success("Summary retrieved successfully", data));
    }
}