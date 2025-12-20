package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.LeadRequestDto;
import com.braininventory.leadsphere.lead_service.dto.LeadResponseDto;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // ✅ Create Lead
    @PostMapping("/create")
    public ResponseEntity<LeadResponseDto> createLead(@RequestBody LeadRequestDto leadRequestDto) {
        LeadResponseDto response = leadService.createLead(leadRequestDto);
        return ResponseEntity.status(201).body(response); // 201 Created
    }

    // ✅ Get Lead by ID
    @GetMapping("/getById/{id}")
    public ResponseEntity<LeadResponseDto> getLeadById(@PathVariable Long id) {
        LeadResponseDto response = leadService.getLeadById(id);
        return ResponseEntity.ok(response); // 200 OK
    }

    // ✅ Get All Leads
    @GetMapping("/getAll")
    public ResponseEntity<List<LeadResponseDto>> getAllLeads() {
        List<LeadResponseDto> response = leadService.getAllLeads();
        return ResponseEntity.ok(response); // 200 OK
    }

    // ✅ Update Lead
    @PutMapping("/update/{id}")
    public ResponseEntity<LeadResponseDto> updateLead(@PathVariable Long id,
                                                      @RequestBody LeadRequestDto leadRequestDto) {
        LeadResponseDto response = leadService.updateLead(id, leadRequestDto);
        return ResponseEntity.ok(response); // 200 OK
    }

    // ✅ Delete Lead
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<LeadResponseDto> deleteLeadById(@PathVariable Long id) {
        LeadResponseDto response = leadService.deleteLeadById(id);
        return ResponseEntity.ok(response); // 200 OK
    }
}