package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface LeadService {

    LeadResponseDto createLead(LeadRequestDto leadRequestDto);

    LeadResponseDto getLeadById(Long id);

    List<LeadResponseDto> getAllLeads();

    LeadResponseDto updateLead(Long id, LeadRequestDto leadRequestDto);

    LeadResponseDto deleteLeadById(Long id);


    // Dashboard api's
    // Returns total leads, converted leads, and conversion percentage
    LeadSummaryDto getLeadSummary();

    // Returns number of leads grouped by lead owner
    List<LeadOwnerCountDto> getLeadsByOwner();

    // Returns number of converted leads grouped by lead owner
    List<LeadOwnerCountDto> getConvertedLeadsByOwner();

    // Returns number of leads grouped by source/stage
    List<LeadSourceCountDto> getLeadsBySource();

    // Returns number of converted leads grouped by source
    List<LeadSourceCountDto> getConvertedLeadsBySource();

    // Returns list of all lead owners for filter dropdowns
    List<String> getLeadOwners();

    // Returns leads filtered by date range and/or owner
    List<LeadDto> getFilteredLeads(LocalDate createdFrom, LocalDate createdTo, String owner);



}