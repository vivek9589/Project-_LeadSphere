package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.dto.LeadDashboardResponse;
import com.braininventory.leadsphere.lead_service.dto.LeadOwnerCountDto;
import com.braininventory.leadsphere.lead_service.dto.LeadSourceCountDto;
import com.braininventory.leadsphere.lead_service.dto.LeadStatsDto;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import com.braininventory.leadsphere.lead_service.exception.DashboardException;
import com.braininventory.leadsphere.lead_service.repository.LeadRepository;
import com.braininventory.leadsphere.lead_service.repository.LeadSpecifications;
import com.braininventory.leadsphere.lead_service.service.LeadDashboardService;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                .leadsByOwner(List.of())
                .leadsBySource(List.of())
                .convertedLeadsByOwner(List.of())
                .convertedLeadsBySource(List.of())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public LeadDashboardResponse getFilteredDashboard(LocalDate start, LocalDate end, String owner) {
        // 1. Single Source of Truth: Fetch ALL filtered leads once
        Specification<Lead> spec = LeadSpecifications.getFilteredLeads(start, end, owner, null);
        List<Lead> allFilteredLeads = leadRepository.findAll(spec);

        // 2. Filter specific lists for stats
        int totalLeads = allFilteredLeads.size();
        List<Lead> wonLeads = allFilteredLeads.stream()
                .filter(l -> l.getStatus() == LeadStatus.WON)
                .toList();

        int convertedLeads = wonLeads.size();
        int conversionRate = (totalLeads == 0) ? 0 : (convertedLeads * 100) / totalLeads;

        // 3. Overall Charts
        List<LeadOwnerCountDto> leadsByOwner = allFilteredLeads.stream()
                .collect(Collectors.groupingBy(Lead::getOwner, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadOwnerCountDto(e.getKey(), e.getValue()))
                .toList();

        List<LeadSourceCountDto> leadsBySource = allFilteredLeads.stream()
                .collect(Collectors.groupingBy(Lead::getSource, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadSourceCountDto(e.getKey(), e.getValue(), getColorForSource(e.getKey())))
                .toList();

        // 4. Converted Charts (Only from WON leads)
        List<LeadOwnerCountDto> convByOwner = wonLeads.stream()
                .collect(Collectors.groupingBy(Lead::getOwner, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadOwnerCountDto(e.getKey(), e.getValue()))
                .toList();

        // Logic added here for Converted Leads by Source
        List<LeadSourceCountDto> convBySource = wonLeads.stream()
                .collect(Collectors.groupingBy(Lead::getSource, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadSourceCountDto(e.getKey(), e.getValue(), getColorForSource(e.getKey())))
                .toList();

        return LeadDashboardResponse.builder()
                .leadStats(new LeadStatsDto(totalLeads, convertedLeads, conversionRate))
                .leadsByOwner(leadsByOwner)
                .leadsBySource(leadsBySource)
                .convertedLeadsByOwner(convByOwner)
                .convertedLeadsBySource(convBySource) // Now fully implemented
                .build();
    }



    // Helper for UI colors - Modern SaaS Palette
    private String getColorForSource(String source) {
        if (source == null) return "#9CA3AF"; // Default Gray

        return switch (source.toUpperCase()) {
            case "WEB" -> "#8B5CF6";             // Soft Purple
            case "REFERRAL" -> "#6366F1";        // Indigo
            case "COMPANY_ENQUIRY" -> "#10B981"; // Emerald Green
            case "UPWORK" -> "#65A30D";          // Lime/Olive (Upwork Brand)
            case "LINKEDIN" -> "#0A66C2";        // LinkedIn Blue
            case "PHONE" -> "#EC4899";           // Pink/Magenta
            case "OTHER" -> "#6B7280";           // Neutral Slate
            default -> "#9CA3AF";                // Medium Gray
        };
    }


}