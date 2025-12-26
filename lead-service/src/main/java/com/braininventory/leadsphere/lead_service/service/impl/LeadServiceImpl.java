package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.dto.*;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import com.braininventory.leadsphere.lead_service.exception.ResourceNotFoundException;
import com.braininventory.leadsphere.lead_service.repository.LeadRepository;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;

    @Autowired
    private final ModelMapper modelMapper;



    @Override
    public LeadResponseDto createLead(LeadRequestDto dto) {
        Lead lead = new Lead();
        mapDtoToEntity(dto, lead);
        //return convertToResponseDto(leadRepository.save(lead));

        Lead savedLead = leadRepository.save(lead);
        leadRepository.flush(); // ensure DB write
        return convertToResponseDto(savedLead);
    }

    @Override
    public LeadResponseDto getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        return convertToResponseDto(lead);
    }

    @Override
    public List<LeadResponseDto> getAllLeads() {
        return leadRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeadResponseDto updateLead(Long id, LeadRequestDto dto) {
        log.info("Updating Lead with ID: {}", id);

        // Use custom exception for the Global Handler to catch
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        // ModelMapper maps only non-null fields if configured correctly
        modelMapper.map(dto, lead);

        Lead savedLead = leadRepository.save(lead);
        return modelMapper.map(savedLead, LeadResponseDto.class);
    }

    @Override
    @Transactional
    public LeadResponseDto deleteLeadById(Long id) {
        log.info("Deleting Lead with ID: {}", id);

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found with id: " + id));

        // Capture data before deletion to return to the user
        LeadResponseDto response = modelMapper.map(lead, LeadResponseDto.class);

        leadRepository.delete(lead);
        return response;
    }

    private void mapDtoToEntity(LeadRequestDto dto, Lead lead) {
        lead.setCompany(dto.getCompany());
        lead.setContactName(dto.getContactName());
        lead.setContactEmail(dto.getContactEmail());
        lead.setContactPhone(dto.getContactPhone());
        lead.setOpportunityName(dto.getOpportunityName());
        lead.setValue(dto.getValue());
        lead.setStatus(dto.getStatus());
        lead.setSource(dto.getSource());
        lead.setOwner(dto.getOwner());
    }

    private LeadResponseDto convertToResponseDto(Lead lead) {
        LeadResponseDto dto = new LeadResponseDto();
        dto.setId(lead.getId());
        dto.setCompany(lead.getCompany());
        dto.setContactName(lead.getContactName());
        dto.setContactEmail(lead.getContactEmail());
        dto.setContactPhone(lead.getContactPhone());
        dto.setOpportunityName(lead.getOpportunityName());
        dto.setValue(lead.getValue());
        dto.setStatus(lead.getStatus());
        dto.setSource(lead.getSource());
        dto.setOwner(lead.getOwner());
        dto.setCreatedAt(lead.getCreatedAt());
        dto.setUpdatedAt(lead.getUpdatedAt());
        return dto;
    }


    @Override
    public LeadSummaryDto getLeadSummary() {
        List<LeadResponseDto> allLeads = getAllLeads();
        Long totalLeads = (long) allLeads.size();

        long convertedLeads = allLeads.stream()
                .filter(lead -> lead.getStatus() == LeadStatus.WON)
                .count();

        Double conversionRate = (double) ((convertedLeads / totalLeads) * 100);


        // build DTO
        LeadSummaryDto summary = new LeadSummaryDto();
        summary.setTotalLeads(totalLeads);
        summary.setConvertedLeads(convertedLeads);
        summary.setConversionRate(conversionRate);

        return summary;
    }

    @Override
    public List<LeadOwnerCountDto> getLeadsByOwner() {

        List<LeadOwnerCountDto> leadsByOwner = leadRepository.getLeadsByOwner();
        return leadsByOwner;

    }

    @Override
    public List<LeadOwnerCountDto> getConvertedLeadsByOwner() {
        return List.of();
    }

    @Override
    public List<LeadSourceCountDto> getLeadsBySource() {
        return List.of();
    }

    @Override
    public List<LeadSourceCountDto> getConvertedLeadsBySource() {
        return List.of();
    }

    @Override
    public List<String> getLeadOwners() {
        return List.of();
    }

    @Override
    public List<LeadDto> getFilteredLeads(LocalDate createdFrom, LocalDate createdTo, String owner) {
        return List.of();
    }


}