package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.dto.*;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import com.braininventory.leadsphere.lead_service.exception.DashboardException;
import com.braininventory.leadsphere.lead_service.repository.LeadRepository;
import com.braininventory.leadsphere.lead_service.repository.LeadSpecifications;
import com.braininventory.leadsphere.lead_service.service.LeadDashboardService;
import com.braininventory.leadsphere.lead_service.service.LeadService;
import com.braininventory.leadsphere.lead_service.service.SalesAnalyticsService;
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
    private final SalesAnalyticsService salesAnalyticsService;

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
            double totalPipelineValue = 0;


            return LeadDashboardResponse.builder()
                    .leadStats(new LeadStatsDto((int) totalLeads, (int) convertedLeads, conversionRate,totalPipelineValue))
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
                .leadStats(new LeadStatsDto(0, 0, 0,0))
                .leadsByOwner(List.of())
                .leadsBySource(List.of())
                .convertedLeadsByOwner(List.of())
                .convertedLeadsBySource(List.of())
                .build();
    }
    @Override
    @Transactional(readOnly = true)
    public LeadDashboardResponse getFilteredDashboard(LocalDate start, LocalDate end, String owner) {
        Specification<Lead> spec = LeadSpecifications.getFilteredLeads(start, end, owner, null);
        List<Lead> allFilteredLeads = leadRepository.findAll(spec);

        int totalLeads = allFilteredLeads.size();
        List<Lead> wonLeads = allFilteredLeads.stream()
                .filter(l -> l.getStatus() == LeadStatus.WON)
                .toList();

        int convertedLeads = wonLeads.size();
        int conversionRate = (totalLeads == 0) ? 0 : (convertedLeads * 100) / totalLeads;

        double totalPipelineValue = wonLeads.stream()
                .mapToDouble(Lead::getValue) // extract the Double field
                .sum();




        // 1. Overall Charts
        List<LeadOwnerCountDto> leadsByOwner = allFilteredLeads.stream()
                .collect(Collectors.groupingBy(l -> l.getOwner() != null ? l.getOwner() : "Unknown", Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadOwnerCountDto(e.getKey(), e.getValue(), getColorForOwner(e.getKey())))
                .toList();

        List<LeadSourceCountDto> leadsBySource = allFilteredLeads.stream()
                .collect(Collectors.groupingBy(l -> l.getSource() != null ? l.getSource() : "Other", Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadSourceCountDto(e.getKey(), e.getValue(), getColorForSource(e.getKey())))
                .toList();

        // 2. Converted Charts
        List<LeadOwnerCountDto> convByOwner = wonLeads.stream()
                .collect(Collectors.groupingBy(l -> l.getOwner() != null ? l.getOwner() : "Unknown", Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadOwnerCountDto(e.getKey(), e.getValue(), getColorForOwner(e.getKey())))
                .toList();

        List<LeadSourceCountDto> convBySource = wonLeads.stream()
                .collect(Collectors.groupingBy(l -> l.getSource() != null ? l.getSource() : "Other", Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadSourceCountDto(e.getKey(), e.getValue(), getColorForSource(e.getKey())))
                .toList();

        return LeadDashboardResponse.builder()
                .leadStats(new LeadStatsDto(totalLeads, convertedLeads, conversionRate,totalPipelineValue))
                .leadsByOwner(leadsByOwner)
                .leadsBySource(leadsBySource)
                .convertedLeadsByOwner(convByOwner)
                .convertedLeadsBySource(convBySource)
                .build();
    }

    private String getColorForSource(String source) {
        if (source == null) return "#9CA3AF";
        return switch (source.toUpperCase()) {
            case "WEB" -> "#E33714";
            case "REFERRAL" -> "#E38D14";
            case "COMPANY_ENQUIRY" -> "#C4E314";
            case "UPWORK" -> "#2CE314";
            case "LINKEDIN" -> "#14E3CB";
            case "PHONE" -> "#148DE3";
            case "PARTNER" -> "#2214E3";
            case "OTHER" -> "#E3146A";
            default -> "#E31437";
        };
    }

    private String getColorForOwner(String ownerName) {
        if (ownerName == null) return "#9CA3AF";
        String[] palette = {"#E33714", "#E38D14", "#C4E314", "#2CE314", "#14E3CB", "#148DE3", "#2214E3", "#E3146A", "#E31437"};
        int index = Math.abs(ownerName.hashCode()) % palette.length;
        return palette[index];
    }


}