package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.dto.LeadRequestDto;
import com.braininventory.leadsphere.lead_service.dto.LeadResponseDto;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.repository.LeadRepository;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;

    @Autowired
    public LeadServiceImpl(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @Override
    public LeadResponseDto createLead(LeadRequestDto leadRequestDto) {
        Lead leadEntity = new Lead();
        leadEntity.setFirstName(leadRequestDto.getFirstName());
        leadEntity.setLastName(leadRequestDto.getLastName());
        leadEntity.setEmail(leadRequestDto.getEmail());
        leadEntity.setPassword(leadRequestDto.getPassword());
        leadEntity.setPhoneNo(leadRequestDto.getPhoneNo());
        leadEntity.setSource(leadRequestDto.getSource());
        leadEntity.setStatus(leadRequestDto.getStatus());
        leadEntity.setAssignedTo(leadRequestDto.getAssignedTo());
        leadEntity.setCreatedAt(new Date());

        Lead savedLead = leadRepository.save(leadEntity);
        return convertToResponseDto(savedLead);
    }

    @Override
    public LeadResponseDto getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));
        return convertToResponseDto(lead);
    }

    @Override
    public List<LeadResponseDto> getAllLeads() {
        return leadRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeadResponseDto updateLead(Long id, LeadRequestDto leadRequestDto) {
        Lead existingLead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));

        existingLead.setFirstName(leadRequestDto.getFirstName());
        existingLead.setLastName(leadRequestDto.getLastName());
        existingLead.setEmail(leadRequestDto.getEmail());
        existingLead.setPassword(leadRequestDto.getPassword());
        existingLead.setPhoneNo(leadRequestDto.getPhoneNo());
        existingLead.setSource(leadRequestDto.getSource());
        existingLead.setStatus(leadRequestDto.getStatus());
        existingLead.setAssignedTo(leadRequestDto.getAssignedTo());

        Lead updatedLead = leadRepository.save(existingLead);
        return convertToResponseDto(updatedLead);
    }

    @Override
    public LeadResponseDto deleteLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found with id: " + id));

        LeadResponseDto response = convertToResponseDto(lead);
        leadRepository.deleteById(id);
        return response;
    }

    // Utility method: Entity → Response DTO
    private LeadResponseDto convertToResponseDto(Lead lead) {
        LeadResponseDto dto = new LeadResponseDto();
        dto.setId(lead.getId());
        dto.setFirstName(lead.getFirstName());
        dto.setLastName(lead.getLastName());
        dto.setEmail(lead.getEmail());
        dto.setPhoneNo(lead.getPhoneNo());
        dto.setSource(lead.getSource());
        dto.setStatus(lead.getStatus());
        dto.setAssignedTo(lead.getAssignedTo());
        dto.setCreatedAt(lead.getCreatedAt());
        return dto;
    }
}