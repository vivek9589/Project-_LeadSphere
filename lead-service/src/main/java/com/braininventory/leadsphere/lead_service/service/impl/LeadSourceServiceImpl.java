package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.entity.LeadSource;
import com.braininventory.leadsphere.lead_service.exception.ResourceConflictException;
import com.braininventory.leadsphere.lead_service.repository.LeadSourceRepository;
import com.braininventory.leadsphere.lead_service.service.LeadSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class LeadSourceServiceImpl implements LeadSourceService {

    @Autowired
    private LeadSourceRepository repository;

    @Override
    public List<LeadSource> getAllActiveSources() {
        log.info("Fetching all active lead sources from database");
        return repository.findByActiveTrue();
    }

    @Override
    @Transactional
    public LeadSource addSource(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Source name cannot be null or empty");
        }

        String normalizedName = name.trim();
        if (repository.existsBysourceNameIgnoreCase(normalizedName)) {
            log.warn("Duplicate source entry attempted: {}", normalizedName);
            throw new ResourceConflictException("Source '" + normalizedName + "' already exists.");
        }

        LeadSource newSource = new LeadSource();
        newSource.setSourceName(normalizedName.toUpperCase());
        newSource.setActive(true);

        log.info("Successfully created new Lead Source: {}", normalizedName);
        return repository.save(newSource);
    }

    @Override
    public List<LeadSource> getSourceSuggestions(String query) {
        log.info("Searching for active sources matching query: {}", query);

        // If query is empty, return all active sources
        if (query == null || query.trim().isEmpty()) {
            log.debug("Empty query, returning all active sources.");
            return repository.findByActiveTrue();
        }

        // Return filtered active sources
        return repository.suggestSources(query);
    }
}