package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.dto.LeadRequestDto;
import com.braininventory.leadsphere.lead_service.dto.LeadResponseDto;

import java.util.List;

public interface LeadService {

    LeadResponseDto createLead(LeadRequestDto leadRequestDto);

    LeadResponseDto getLeadById(Long id);

    List<LeadResponseDto> getAllLeads();

    LeadResponseDto updateLead(Long id, LeadRequestDto leadRequestDto);

    LeadResponseDto deleteLeadById(Long id);
}