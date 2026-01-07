package com.braininventory.leadsphere.lead_service.service;

import com.braininventory.leadsphere.lead_service.dto.SalesTargetDTO;
import com.braininventory.leadsphere.lead_service.entity.SalesTarget;

public interface SalesTargetService {

    SalesTarget upsertTarget(Long userId, SalesTargetDTO dto);

    SalesTarget getTarget(Long userId, Integer month, Integer year);
}
