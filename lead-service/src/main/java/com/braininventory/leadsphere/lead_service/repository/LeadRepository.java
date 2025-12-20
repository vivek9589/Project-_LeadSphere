package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeadRepository extends JpaRepository<Lead,Long> {
}
