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



    // count by
    long countByStatus(LeadStatus status);

    @Query("SELECT DISTINCT l.ownerId as ownerId, l.owner as ownerName " +
            "FROM Lead l " +
            "WHERE l.ownerId IS NOT NULL AND l.owner IS NOT NULL")
    List<OwnerFilterProjection> findAllUniqueOwners();


    // query is existByCompanyNameAndCotactEmail
    boolean existsByContactEmailAndCompany(String contactEmail, String company);

    // Helper to resolve Name from ID
    @Query("SELECT DISTINCT l.owner FROM Lead l WHERE l.ownerId = :ownerId")
    String findOwnerNameById(@Param("ownerId") Long ownerId);

    List<Lead> findByOwner(String ownerName);

    // getalll owner
    @Query("SELECT DISTINCT l.owner FROM Lead l")
    List<String> findAllOwners();


    // Sum WON deals (Value) for the current user and month
    @Query("SELECT SUM(l.value) FROM Lead l WHERE l.ownerId = :userId " +
            "AND l.status = 'WON' AND MONTH(l.actualCloseDate) = :m AND YEAR(l.actualCloseDate) = 2026")
    Double sumWonValueById(Long userId, int m);

    // Sum Forecast/Pipeline (Value) for the current user and month
    @Query("SELECT SUM(l.value) FROM Lead l WHERE l.ownerId = :userId " +
            "AND l.status NOT IN ('WON', 'LOST', 'REJECTED') " +
            "AND MONTH(l.createdAt) = :m AND YEAR(l.createdAt) = 2026")
    Double sumPipelineValueById(Long userId, int m);


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



    // filter's queries
//    List<Lead> findByContactNameContainingIgnoreCase(String contactName);
//    List<Lead> findByContactEmailContainingIgnoreCase(String contactEmail);
//    List<Lead> findByCompanyContainingIgnoreCase(String company);
//    List<Lead> findByOpportunityNameContainingIgnoreCase(String opportunityName);


    @Query("SELECT l FROM Lead l WHERE " +
            "(:ownerId IS NULL OR l.ownerId = :ownerId) AND (" +
            "LOWER(l.contactName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.contactEmail) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.company) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(l.opportunityName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Lead> searchLeadsByGlobalCriteria(
            @Param("searchTerm") String searchTerm,
            @Param("ownerId") Long ownerId);

    // Also need a scoped version for the 'findAll' equivalent
    List<Lead> findByOwnerId(Long ownerId);
}


