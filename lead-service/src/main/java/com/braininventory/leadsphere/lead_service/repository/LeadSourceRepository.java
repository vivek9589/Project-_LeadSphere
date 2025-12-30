package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.entity.LeadSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadSourceRepository extends JpaRepository<LeadSource, Long> {
    List<LeadSource> findByActiveTrue();
    boolean existsBysourceNameIgnoreCase(String name);
}