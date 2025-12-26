package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.lead_service.dto.LeadOwnerCountDto;
import com.braininventory.leadsphere.lead_service.dto.LeadSourceCountDto;
import com.braininventory.leadsphere.lead_service.dto.LeadStatsDto;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import com.braininventory.leadsphere.lead_service.exception.DashboardException;
import com.braininventory.leadsphere.lead_service.repository.LeadRepository;
import com.braininventory.leadsphere.lead_service.service.LeadDashboardService;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Builder
public class LeadDashboardServiceImpl implements LeadDashboardService {

    private final LeadRepository leadRepository;

    @Override
    @Transactional(readOnly = true)
    public LeadDashboardResponse getLeadDashboard() {
        log.info("Fetching dashboard analytics...");

        try {
            long totalLeads = leadRepository.count();

            // If there's no data at all, we might want to inform the frontend specifically
            if (totalLeads == 0) {
                log.warn("No lead data found in the system.");
                return createEmptyResponse();
            }

            long convertedLeads = leadRepository.countByStatus(LeadStatus.WON);
            int conversionRate = (int) ((convertedLeads * 100) / totalLeads);

            return LeadDashboardResponse.builder()
                    .leadStats(new LeadStatsDto((int) totalLeads, (int) convertedLeads, conversionRate))
                    .leadsByOwner(leadRepository.getLeadsByOwner())
                    .leadsBySource(leadRepository.getLeadsBySource())
                    .convertedLeadsByOwner(leadRepository.getConvertedLeadsByOwner(LeadStatus.WON))
                    .convertedLeadsBySource(leadRepository.getConvertedLeadsBySource(LeadStatus.WON))
                    .build();

        } catch (DataAccessException e) {
            log.error("Database error while fetching dashboard stats: {}", e.getMessage());
            throw new DashboardException("Database connection failed while aggregating dashboard data");
        }
    }

    private LeadDashboardResponse createEmptyResponse() {
        return LeadDashboardResponse.builder()
                .leadStats(new LeadStatsDto(0, 0, 0))
                .leadsByOwner(Collections.emptyList())
                .leadsBySource(Collections.emptyList())
                .build();
    }
}