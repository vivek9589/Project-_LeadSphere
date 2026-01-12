package com.braininventory.leadsphere.lead_service.controller;

import com.braininventory.leadsphere.lead_service.dto.SalesTargetDTO;
import com.braininventory.leadsphere.lead_service.dto.StandardResponse;
import com.braininventory.leadsphere.lead_service.entity.SalesTarget;
import com.braininventory.leadsphere.lead_service.service.SalesTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/leads")
public class SalesTargetController {

    @Autowired
    private SalesTargetService salesTargetService;

    // Matches POST /leads/targets/{userId}
// Matches PATCH /leads/targets/{userId}
    @PutMapping("/set-target/{userId}")
    public ResponseEntity<StandardResponse<SalesTarget>> patchTarget(
            @PathVariable Long userId,
            @RequestBody SalesTargetDTO targetDto) {

        SalesTarget updated = salesTargetService.upsertTarget(userId,targetDto);
        return ResponseEntity.ok(StandardResponse.ok(updated, "Sales target updated successfully"));
    }

    // Matches GET /leads/targets/current/{userId}
    @GetMapping("/get-target/{userId}")
    public ResponseEntity<StandardResponse<SalesTarget>> getMonthlyTarget(@PathVariable Long userId) {
        LocalDate now = LocalDate.now();
        SalesTarget target = salesTargetService.getTarget(userId, now.getMonthValue(), now.getYear());

        return ResponseEntity.ok(StandardResponse.ok(target, "Current month target fetched"));
    }
}