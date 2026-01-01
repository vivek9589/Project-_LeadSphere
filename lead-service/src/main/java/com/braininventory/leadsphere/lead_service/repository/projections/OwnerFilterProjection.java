package com.braininventory.leadsphere.lead_service.repository.projections;

public interface OwnerFilterProjection {
    Long getOwnerId();   // Maps to owner_id
    String getOwnerName(); // Maps to the alias we define
}