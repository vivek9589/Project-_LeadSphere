package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.entity.Lead;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LeadSpecifications {
    public static Specification<Lead> getFilteredLeads(LocalDate start, LocalDate end, Long ownerId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Date Range: Start of the day (00:00:00)
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start.atStartOfDay()));
            }

            // 2. Date Range: End of the day (23:59:59.999...)
            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end.atTime(LocalTime.MAX)));
            }

            // 3. Owner Filter: Uses the ownerId field defined in your Entity
            if (ownerId != null) {
                predicates.add(cb.equal(root.get("ownerId"), ownerId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}