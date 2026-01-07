package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.entity.SalesTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SalesTargetRepository extends JpaRepository<SalesTarget, Long> {

    // This method signature tells Spring to generate:
    // SELECT * FROM sales_targets WHERE user_id = ? AND target_month = ? AND target_year = ?
    Optional<SalesTarget> findByUserIdAndTargetMonthAndTargetYear(Long userId, Integer targetMonth, Integer targetYear);


}