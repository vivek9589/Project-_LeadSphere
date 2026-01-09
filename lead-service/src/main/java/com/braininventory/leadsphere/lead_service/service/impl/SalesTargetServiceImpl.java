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

        target.setUserId(userId);

        if (dto.getMonthlyTarget() != null) {
            target.setMonthlyTarget(dto.getMonthlyTarget());
        }
        if (dto.getYearlyTarget() != null) {
            target.setYearlyTarget(dto.getYearlyTarget());

            // Reflect yearly target into monthly target as the same value
           if(dto.getMonthlyTarget() == null)
           {
               target.setMonthlyTarget(dto.getYearlyTarget());
           }

        }
        if (dto.getQuarterlyTarget() != null) {
            target.setQuarterlyTarget(dto.getQuarterlyTarget());
        }
        if (dto.getTargetMonth() != null) {
            target.setTargetMonth(dto.getTargetMonth());
        }
        if (dto.getTargetYear() != null) {
            target.setTargetYear(dto.getTargetYear());
        }

        return salesTargetRepository.save(target);

    }



    public SalesTarget getTarget(Long userId, Integer month, Integer year) {
        return salesTargetRepository
                .findByUserIdAndTargetMonthAndTargetYear(userId, month, year)
                .orElse(null);
    }
}
