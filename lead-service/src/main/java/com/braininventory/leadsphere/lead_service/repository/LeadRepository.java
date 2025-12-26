package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.dto.LeadOwnerCountDto;
import com.braininventory.leadsphere.lead_service.dto.LeadSourceCountDto;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LeadRepository extends JpaRepository<Lead,Long> {

    // long findBystatus(LeadStatus status);
    // Change findBystatus to countByStatus
    long countByStatus(LeadStatus status);



    // getLeadsByOwner

    @Query("SELECT new com.braininventory.leadsphere.lead_service.dto.LeadOwnerCountDto(l.owner, COUNT(l)) " +
            "FROM Lead l " +
            "GROUP BY l.owner")
    List<LeadOwnerCountDto> getLeadsByOwner();



    // getLeadsBySource
    @Query("SELECT new com.braininventory.leadsphere.lead_service.dto.LeadSourceCountDto(l.source, COUNT(l)) " +
            "FROM Lead l GROUP BY l.source")
    List<LeadSourceCountDto> getLeadsBySource();



    @Query("SELECT new com.braininventory.leadsphere.lead_service.dto.LeadOwnerCountDto(l.owner, COUNT(l)) " +
            "FROM Lead l WHERE l.status = :status GROUP BY l.owner")
    List<LeadOwnerCountDto> getConvertedLeadsByOwner(@Param("status") LeadStatus status);




    @Query("SELECT new com.braininventory.leadsphere.lead_service.dto.LeadSourceCountDto(l.source, COUNT(l)) " +
            "FROM Lead l WHERE l.status = :status GROUP BY l.source")
    List<LeadSourceCountDto> getConvertedLeadsBySource(@Param("status") LeadStatus status);






}
