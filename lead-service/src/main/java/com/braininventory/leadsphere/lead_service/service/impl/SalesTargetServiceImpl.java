package com.braininventory.leadsphere.lead_service.service.impl;

import com.braininventory.leadsphere.lead_service.dto.SalesTargetDTO;
import com.braininventory.leadsphere.lead_service.entity.SalesTarget;
import com.braininventory.leadsphere.lead_service.repository.SalesTargetRepository;
import com.braininventory.leadsphere.lead_service.service.SalesTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class SalesTargetServiceImpl implements SalesTargetService {

    @Autowired
    private SalesTargetRepository salesTargetRepository;

    public SalesTarget upsertTarget(Long userId, SalesTargetDTO dto) {
        // Find existing record for that specific month/year
        SalesTarget target = salesTargetRepository
                .findByUserIdAndTargetMonthAndTargetYear(userId, dto.getTargetMonth(), dto.getTargetYear())
                .orElse(new SalesTarget());

        // Update fields
        target.setUserId(userId);
        target.setMonthlyTarget(dto.getMonthlyTarget());
        target.setQuarterlyTarget(dto.getQuarterlyTarget());
        target.setTargetMonth(dto.getTargetMonth());
        target.setTargetYear(dto.getTargetYear());

        return salesTargetRepository.save(target);
    }

    public SalesTarget getTarget(Long userId, Integer month, Integer year) {
        return salesTargetRepository
                .findByUserIdAndTargetMonthAndTargetYear(userId, month, year)
                .orElse(null);
    }
}
