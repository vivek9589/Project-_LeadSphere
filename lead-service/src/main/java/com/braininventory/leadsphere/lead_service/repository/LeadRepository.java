package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LeadRepository extends JpaRepository<Lead,Long> {

    long findBystatus(LeadStatus status);
}
