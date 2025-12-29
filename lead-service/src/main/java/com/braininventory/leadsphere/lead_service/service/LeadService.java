package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.dto.*;

import java.util.List;

public interface LeadService {

    LeadResponseDto createLead(LeadRequestDto leadRequestDto);

    LeadResponseDto getLeadById(Long id);

    List<LeadResponseDto> getAllLeads();

    LeadResponseDto updateLead(Long id, LeadRequestDto leadRequestDto);

    LeadResponseDto deleteLeadById(Long id);

   // List<LeadResponseDto> getLeadsByOwnerName(Long id);


    List<LeadResponseDto> getLeadsByOwnerId(Long id);



}