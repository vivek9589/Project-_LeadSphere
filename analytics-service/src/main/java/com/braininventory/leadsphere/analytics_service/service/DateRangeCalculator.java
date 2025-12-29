package com.braininventory.leadsphere.analytics_service.service;


import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
public class DateRangeCalculator {
    public LocalDate getStartDate(String range) {
        LocalDate now = LocalDate.now();
        return switch (range.toLowerCase()) {
            case "today" -> now;
            case "this_week" -> now.with(java.time.DayOfWeek.MONDAY);
            case "this_month" -> now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
            case "this_year" -> now.with(java.time.temporal.TemporalAdjusters.firstDayOfYear());
            default -> now.minusMonths(1);
        };
    }
}