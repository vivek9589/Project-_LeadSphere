package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.dto.*;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.exception.DuplicateLeadException;
import com.braininventory.leadsphere.lead_service.exception.LeadCreationException;
import com.braininventory.leadsphere.lead_service.exception.ResourceNotFoundException;
import com.braininventory.leadsphere.lead_service.feign.UserClient;
import com.braininventory.leadsphere.lead_service.repository.LeadRepository;
import com.braininventory.leadsphere.lead_service.repository.projections.OwnerFilterProjection;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private UserClient userClient;



    @Override
    @Transactional
    public LeadResponseDto createLead(LeadRequestDto dto) {
        log.info("Attempting to create lead with email={} and company={}", dto.getContactEmail(), dto.getCompany());

        try {
            boolean alreadyExist = leadRepository.existsByContactEmailAndCompany(
                    dto.getContactEmail(), dto.getCompany());

            if (alreadyExist) {
                log.warn("Duplicate lead detected for email={} and company={}", dto.getContactEmail(), dto.getCompany());
                throw new DuplicateLeadException(
                        "Lead with email " + dto.getContactEmail() + " and company " + dto.getCompany() + " already exists"
                );
            }

            Lead lead = new Lead();
            mapDtoToEntity(dto, lead);

            Lead savedLead = leadRepository.save(lead);
            leadRepository.flush(); // ensure DB write

            log.info("Lead successfully created with id={}", savedLead.getId());
            return convertToResponseDto(savedLead);

        } catch (DuplicateLeadException ex) {
            // Custom business exception — rethrow for GlobalExceptionHandler
            throw ex;
        } catch (Exception ex) {
            // Catch unexpected errors, log them, and wrap in a custom exception
            log.error("Unexpected error occurred while creating lead: {}", ex.getMessage(), ex);
            throw new LeadCreationException("Failed to create lead due to an unexpected error", ex);
        }
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

    @Override
    public List<LeadResponseDto> getLeadsByOwnerId(Long id) {
        UserResponseDto salesUser = userClient.getSalesUserById(id);
        String ownerName = salesUser.getFirstName() + " " + salesUser.getLastName();

        log.info("Fetching leads from repository for ownerName: {}", ownerName);
        List<Lead> byOwner = leadRepository.findByOwner(ownerName);

        log.debug("Repository returned {} leads for ownerId {}", byOwner.size(), id);

        return byOwner.stream()
                .map(this::convertToResponseDto) // convert each Lead to LeadResponseDto
                .collect(Collectors.toList());


    }

    @Override
    public List<OwnerFilterProjection> getOwnerFilterList() {
        log.debug("Executing query to find distinct owner names and IDs from leads table");

        List<OwnerFilterProjection> owners = leadRepository.findAllUniqueOwners();

        if (owners.isEmpty()) {
            log.warn("No lead owners found in the database.");
        }

        return owners;
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
        lead.setOwnerId(dto.getOwnerId());
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
        dto.setOwnerId(lead.getOwnerId());
        dto.setOwner(lead.getOwner());
        dto.setCreatedAt(lead.getCreatedAt());
        dto.setUpdatedAt(lead.getUpdatedAt());
        return dto;
    }



}