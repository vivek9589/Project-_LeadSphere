


package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LeadSpecifications {
    public static Specification<Lead> getFilteredLeads(LocalDate start, LocalDate end, String ownerName, Long ownerId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (start != null) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start.atStartOfDay()));
            if (end != null) predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end.atTime(23, 59, 59)));
            if (ownerId != null) predicates.add(cb.equal(root.get("ownerId"), ownerId));
            else if (ownerName != null) predicates.add(cb.equal(root.get("owner"), ownerName));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

