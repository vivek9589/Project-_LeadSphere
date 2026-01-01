package com.braininventory.leadsphere.analytics_service.service;


import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component
public class DateRangeCalculator {
    public LocalDate getStartDate(String range) {
        LocalDate now = LocalDate.now();
        if (range == null) return now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());

        return switch (range.toLowerCase()) {
            case "today" -> now;
            case "this_week" -> now.with(java.time.DayOfWeek.MONDAY);
            case "this_month" -> now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
            case "this_year" -> now.with(java.time.temporal.TemporalAdjusters.firstDayOfYear());
            case "total" -> LocalDate.of(2000, 1, 1); // Or return null if your Spec handles it
            default -> now.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        };
    }
}