package com.braininventory.leadsphere.lead_service.repository;

import com.braininventory.leadsphere.lead_service.entity.LeadSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadSourceRepository extends JpaRepository<LeadSource, Long> {
    List<LeadSource> findByActiveTrue();
    boolean existsBysourceNameIgnoreCase(String name);


    // Suggestion query (Fixed entity name to LeadSource)
    @Query("SELECT s FROM LeadSource s WHERE s.active = true " +
            "AND LOWER(s.sourceName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<LeadSource> suggestSources(@Param("query") String query);
}