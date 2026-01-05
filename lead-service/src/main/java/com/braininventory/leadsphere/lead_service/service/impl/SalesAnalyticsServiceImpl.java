package com.braininventory.leadsphere.lead_service.service.impl;


import com.braininventory.leadsphere.lead_service.dto.*;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.entity.SalesTarget;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import com.braininventory.leadsphere.lead_service.repository.LeadRepository;
import com.braininventory.leadsphere.lead_service.repository.LeadSpecifications;
import com.braininventory.leadsphere.lead_service.repository.SalesTargetRepository;
import com.braininventory.leadsphere.lead_service.service.SalesAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class SalesAnalyticsServiceImpl implements SalesAnalyticsService {

    private final LeadRepository leadRepository;
    private final SalesTargetRepository targetRepository;

    @Override
    @Transactional(readOnly = true)
    public UserPerformanceDashboardResponseDTO getConsolidatedUserDashboard(LocalDate start, LocalDate end, Long ownerId) {

        // 1. Fetch leads using the corrected Specification signature
        Specification<Lead> spec = LeadSpecifications.getFilteredLeads(start, end, null, ownerId);
        List<Lead> filteredLeads = leadRepository.findAll(spec);

        List<Lead> wonLeads = filteredLeads.stream()
                .filter(l -> l.getStatus() == LeadStatus.WON)
                .toList();

        // 2. Fetch Performance Metrics from existing calculation logic
        SalesPerformanceDTO performance = calculateUserPerformanceMetrics(ownerId);

        // 3. Build the NEW Response DTO
        return UserPerformanceDashboardResponseDTO.builder()
                .leadStats(new LeadStatsDto(
                        filteredLeads.size(),
                        wonLeads.size(),
                        (filteredLeads.isEmpty()) ? 0 : (wonLeads.size() * 100) / filteredLeads.size()
                ))
                .leadsBySource(processSourceCounts(filteredLeads))
                .convertedLeadsBySource(processSourceCounts(wonLeads))
                .monthlyAttainment(performance.getMonthlyAttainment())
                .quarterlyTrend(performance.getQuarterlyTrend())
                .build();
    }

    private List<LeadSourceCountDto> processSourceCounts(List<Lead> leads) {
        return leads.stream()
                .filter(l -> l.getSource() != null)
                .collect(Collectors.groupingBy(l -> l.getSource().toUpperCase(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> new LeadSourceCountDto(e.getKey(), e.getValue(), getColorForSource(e.getKey())))
                .toList();
    }

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

    @Override
    public SalesPerformanceDTO calculateUserPerformanceMetrics(Long userId) {
        log.info("Starting performance calculation for owner_id: {}", userId);

        try {
            // Using current context date: 2026-01-02
            LocalDate now = LocalDate.now();

            // 1. Calculate Monthly Attainment (Gauge Chart)
            MonthlyAttainmentDTO attainment = calculateMonthlyAttainment(userId, now);

            // 2. Calculate Quarterly Trend (Bar Graph)
            List<QuarterlyTrendDTO> trend = calculateQuarterlyTrend(userId, now);

            log.info("Dashboard data calculated successfully for user: {}", userId);
            return new SalesPerformanceDTO(attainment, trend);

        } catch (Exception e) {
            log.error("Critical error calculating metrics for user {}: {}", userId, e.getMessage());
            // Use your custom Runtime or AnalyticsException
            throw new RuntimeException("Analytics calculation failed for user: " + userId);
        }
    }

    private MonthlyAttainmentDTO calculateMonthlyAttainment(Long userId, LocalDate now) {
        // Fetch target: Year 2026, Month 1 (January)
        SalesTarget target = targetRepository.findByUserIdAndTargetMonthAndTargetYear(
                userId, now.getMonthValue(), now.getYear()).orElse(new SalesTarget());

        // Achieved = SUM of WON leads in Jan 2026
        Double achieved = leadRepository.sumWonValueById(userId, now.getMonthValue());
        achieved = (achieved != null) ? achieved : 0.0;

        double targetVal = (target.getMonthlyTarget() != null) ? target.getMonthlyTarget() : 0.0;

        // Calculate percentage (e.g., if Alisha won 35,000 against 100,000 target = 35%)
        double percentage = (targetVal > 0) ? (achieved / targetVal) * 100 : 0.0;

        return MonthlyAttainmentDTO.builder()
                .targetValue(targetVal)
                .achievedValue(achieved)
                .attainmentPercentage(percentage)
                .isOverAchieved(percentage > 100)
                .build();
    }

    private List<QuarterlyTrendDTO> calculateQuarterlyTrend(Long userId, LocalDate now) {
        List<QuarterlyTrendDTO> trendList = new ArrayList<>();

        // Logic: Determine the start of the current quarter (1 for Q1, 4 for Q2, etc.)
        int quarterStartMonth = ((now.getMonthValue() - 1) / 3) * 3 + 1;

        for (int i = 0; i < 3; i++) {
            int targetMonth = quarterStartMonth + i;
            String label = Month.of(targetMonth).name().substring(0, 3); // JAN, FEB, MAR

            // Fetch the specific target for the current year and the specific month in the loop
            SalesTarget salesTarget = targetRepository.findByUserIdAndTargetMonthAndTargetYear(
                    userId, targetMonth, now.getYear()).orElse(new SalesTarget());

            // Extract values with null-safety
            Double monthlyTarget = (salesTarget.getMonthlyTarget() != null) ? salesTarget.getMonthlyTarget() : 0.0;
            Double wonValue = leadRepository.sumWonValueById(userId, targetMonth);
            Double pipeValue = leadRepository.sumPipelineValueById(userId, targetMonth);

            // Using your @AllArgsConstructor: monthName, targetValue, closedRevenue, pipelineValue
            trendList.add(new QuarterlyTrendDTO(
                    label,
                    monthlyTarget,
                    wonValue != null ? wonValue : 0.0,
                    pipeValue != null ? pipeValue : 0.0
            ));
        }
        return trendList;
    }
}