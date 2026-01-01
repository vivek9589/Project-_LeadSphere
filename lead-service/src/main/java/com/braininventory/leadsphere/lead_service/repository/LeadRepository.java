package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.dto.LeadOwnerCountDto;
import com.braininventory.leadsphere.lead_service.dto.LeadResponseDto;
import com.braininventory.leadsphere.lead_service.dto.LeadSourceCountDto;
import com.braininventory.leadsphere.lead_service.entity.Lead;
import com.braininventory.leadsphere.lead_service.enums.LeadStatus;
import com.braininventory.leadsphere.lead_service.repository.projections.OwnerFilterProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LeadRepository extends JpaRepository<Lead,Long> , JpaSpecificationExecutor<Lead> {

    // long findBystatus(LeadStatus status);
    // Change findBystatus to countByStatus
    long countByStatus(LeadStatus status);

    @Query("SELECT DISTINCT l.ownerId as ownerId, l.owner as ownerName " +
            "FROM Lead l " +
            "WHERE l.ownerId IS NOT NULL AND l.owner IS NOT NULL")
    List<OwnerFilterProjection> findAllUniqueOwners();

    List<Lead> findByOwner(String ownerName);

    // getalll owner
    @Query("SELECT DISTINCT l.owner FROM Lead l")
    List<String> findAllOwners();



    @Query("SELECT new com.braininventory.leadsphere.lead_service.dto.LeadOwnerCountDto(l.owner, COUNT(l)) " +
            "FROM Lead l " +
            "WHERE l.owner = :ownerName " +
            "GROUP BY l.owner")

    List<LeadResponseDto> getLeadsByOwnerName(@Param("ownerName") String ownerName);

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



    /*   gemini code

    @Query("SELECT new com.leadsphere.dto.LeadOwnerCountDto(l.owner, COUNT(l)) " +
            "FROM Lead l WHERE l.createdAt BETWEEN :start AND :end GROUP BY l.owner")
    List<LeadOwnerCountDto> getLeadsByOwnerFiltered(LocalDateTime start, LocalDateTime end);

*/


}
