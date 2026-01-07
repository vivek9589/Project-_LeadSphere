package com.braininventory.leadsphere.user_service.feign;

import com.braininventory.leadsphere.user_service.config.FeignConfig;
import com.braininventory.leadsphere.user_service.dto.SalesTargetDTO;
import com.braininventory.leadsphere.user_service.dto.StandardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "lead-service", configuration = FeignConfig.class)
public interface LeadServiceClient {

    // Must match the POST in SalesTargetController
    @PostMapping("/leads/set-target/{userId}")
    StandardResponse<SalesTargetDTO> setTarget(
            @PathVariable("userId") Long userId,
            @RequestBody SalesTargetDTO targetDto
    );

    // Must match the GET in SalesTargetController
    @GetMapping("/leads/get-target/{userId}")
    StandardResponse<SalesTargetDTO> getMonthlyTarget(
            @PathVariable("userId") Long userId
    );
}