package com.braininventory.leadsphere.user_service.feign;

import com.braininventory.leadsphere.user_service.config.FeignConfig;
import com.braininventory.leadsphere.user_service.dto.SalesTargetDTO;
import com.braininventory.leadsphere.user_service.dto.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "lead-service", configuration = FeignConfig.class)
public interface LeadServiceClient {


    @PutMapping("/leads/set-target/{userId}")
    StandardResponse<SalesTargetDTO> setTarget(
            @PathVariable("userId") Long userId,
            @RequestBody SalesTargetDTO targetDto
    );

    @GetMapping("/leads/get-target/{userId}")
    StandardResponse<SalesTargetDTO> getMonthlyTarget(
            @PathVariable("userId") Long userId
    );
}