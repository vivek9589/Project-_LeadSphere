package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.entity.LeadSource;

import java.util.List;

public interface LeadSourceService {
    List<LeadSource> getAllActiveSources();
    LeadSource addSource(String name);
}