package com.braininventory.leadsphere.auth_service;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class HealthController {


    @GetMapping("/health-check")
    public String healthCheck()
    {
        return "Auth service --> Alll good, What about you ???";
    }
}
