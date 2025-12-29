package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;



public class LeadSpecifications {
    public static Specification<Lead> getFilteredLeads(LocalDate start, LocalDate end, String owner, LeadStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Date Range Filter - Precision fix
            if (start != null && end != null) {
                // Captures from 00:00:00.000000 to 23:59:59.999999
                predicates.add(cb.between(root.get("createdAt"),
                        start.atStartOfDay(),
                        end.atTime(LocalTime.MAX)));
            }

            // Owner Filter
            if (owner != null && !owner.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("owner"), owner.trim()));
            }

            // Status Filter
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}